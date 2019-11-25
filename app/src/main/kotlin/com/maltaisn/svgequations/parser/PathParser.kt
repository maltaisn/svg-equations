/*
 * Copyright 2019 Nicolas Maltais
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maltaisn.svgequations.parser

import com.maltaisn.svgequations.element.*
import java.util.*


/**
 * Used to parse SVG tokens into SVG element classes.
 * More info on SVG commands at [https://www.w3.org/TR/SVG/paths.html#PathDataBNF].
 *
 * @param lenient If lenient, parser will ignore non-fatal errors.
 */
class PathParser(val lenient: Boolean) {

    private lateinit var tokens: PathTokens

    private val elements = LinkedList<Element>()

    /**
     * Parse SVG path [tokens].
     * @throws SvgParseException Thrown on parse errors.
     */
    fun parse(tokens: PathTokens): Path {
        this.tokens = tokens
        elements.clear()

        var lastPoint = Vec2()
        var startPoint = Vec2()

        for (command in tokens.commands) {
            val absolute = command.isUpperCase()
            var point = Vec2()

            // Create element.
            when (command.toUpperCase()) {
                'M' -> {
                    // Start path.
                    point = if (command == 'M') readPoint() else readRelativePoint(lastPoint)
                    startPoint = point
                }
                'Z' -> {
                    // Close path with straight line
                    createElement(Line(lastPoint, startPoint))
                }
                'L' -> createElement(absolute, lastPoint) { readPoint ->
                    // Line
                    point = readPoint()
                    Line(lastPoint, point)
                }
                'H' -> createElement(absolute, lastPoint.x) { readValue ->
                    // Horizontal line
                    point = Vec2(readValue(), lastPoint.y)
                    Line(lastPoint, point)
                }
                'V' -> createElement(absolute, lastPoint.y) { readValue ->
                    // Vertical line
                    point = Vec2(lastPoint.x, readValue())
                    Line(lastPoint, point)
                }
                'Q' -> createElement(absolute, lastPoint) { readPoint ->
                    // Quadratic bezier curve
                    val c1 = readPoint()
                    point = readPoint()
                    Curve(lastPoint, point, listOf(c1))
                }
                'T' -> createElement(absolute, lastPoint) { readPoint ->
                    // Shorthand quadratic bezier curve
                    val c1 = getShorthandControl(elements.last, 1, lastPoint)
                    point = readPoint()
                    Curve(lastPoint, point, listOf(c1))
                }
                'C' -> createElement(absolute, lastPoint) { readPoint ->
                    // Cubic bezier curve
                    val c1 = readPoint()
                    val c2 = readPoint()
                    point = readPoint()
                    Curve(lastPoint, point, listOf(c1, c2))
                }
                'S' -> createElement(absolute, lastPoint) { readPoint ->
                    // Shorthand cubic bezier curve
                    val c1 = getShorthandControl(elements.last, 2, lastPoint)
                    val c2 = readPoint()
                    point = readPoint()
                    Curve(lastPoint, point, listOf(c1, c2))
                }
                'A' -> createElement(absolute, lastPoint) { readPoint ->
                    val radius = this@PathParser.readPoint()
                    val rotation = Math.toRadians(readValue())
                    val largeArc = readBoolean()
                    val sweep = readBoolean()
                    point = readPoint()
                    Arc(lastPoint, point, radius, rotation, largeArc, sweep)
                }
                else -> error("Unknown command")  // Should never happen.
            }

            lastPoint = point
        }

        return Path(LinkedList(elements))
    }

    private fun createElement(element: Element) {
        if (element.start != element.end) {
            // Don't add element if start point matches end point which means element is invisible.
            elements += element
        }
    }

    private inline fun createElement(absolute: Boolean, lastPoint: Vec2,
                                     block: (readPoint: () -> Vec2) -> Element) =
            createElement(if (absolute) {
                block { readPoint() }
            } else {
                block { readRelativePoint(lastPoint) }
            })

    private inline fun createElement(absolute: Boolean, lastValue: Double,
                                     block: (readValue: () -> Double) -> Element) =
            createElement(if (absolute) {
                block { readValue() }
            } else {
                block { readValue() + lastValue }
            })

    private fun readValue() = tokens.values.pop()

    private fun readBoolean() = when (val value = readValue()) {
        0.0 -> false
        1.0 -> true
        else -> if (lenient) {
            // If lenient use 'false' for wrong boolean values.
            false
        } else {
            parseError("Invalid boolean value '$value'.")
        }
    }

    private fun readPoint() = Vec2(readValue(), readValue())

    private fun readRelativePoint(lastPoint: Vec2) =
            Vec2(lastPoint.x + readValue(), lastPoint.y + readValue())

    private fun getShorthandControl(lastElement: Element, controls: Int, currentPoint: Vec2) =
            if (lastElement is Curve && lastElement.controls.size == controls) {
                // The control point is the reflection of the second control point on
                // the previous cubic command relative to the current point.
                currentPoint + (currentPoint - lastElement.controls.last())
            } else {
                // Assume control point is coincident with the current point.
                currentPoint
            }

}


