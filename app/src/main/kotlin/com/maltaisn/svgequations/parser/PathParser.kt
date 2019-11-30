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

import com.maltaisn.svgequations.Curve
import com.maltaisn.svgequations.Path
import com.maltaisn.svgequations.math.Vec2
import java.util.*


/**
 * Used to parse SVG tokens into curve objects.
 * More info on SVG commands at [https://www.w3.org/TR/SVG/paths.html#PathDataBNF].
 *
 * @param lenient If lenient, parser will ignore non-fatal errors.
 */
class PathParser(val lenient: Boolean) {

    private lateinit var tokens: PathTokens

    private val curves = LinkedList<Curve>()

    /**
     * Parse SVG path [tokens].
     * @throws SvgParseException Thrown on parse errors.
     */
    fun parse(tokens: PathTokens): Path {
        this.tokens = tokens
        curves.clear()

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
                    addCurve(lastPoint, startPoint)
                }
                'L' -> withPathContext(absolute, lastPoint) { readPoint ->
                    // Line
                    point = readPoint()
                    addCurve(lastPoint, point)
                }
                'H' -> withPathContext(absolute, lastPoint.x) { readValue ->
                    // Horizontal line
                    point = Vec2(readValue(), lastPoint.y)
                    addCurve(lastPoint, point)
                }
                'V' -> withPathContext(absolute, lastPoint.y) { readValue ->
                    // Vertical line
                    point = Vec2(lastPoint.x, readValue())
                    addCurve(lastPoint, point)
                }
                'Q' -> withPathContext(absolute, lastPoint) { readPoint ->
                    // Quadratic bezier curve
                    val c1 = readPoint()
                    point = readPoint()
                    addCurve(lastPoint, c1, point)
                }
                'T' -> withPathContext(absolute, lastPoint) { readPoint ->
                    // Shorthand quadratic bezier curve
                    val c1 = getShorthandControl(curves.last, 3, lastPoint)
                    point = readPoint()
                    addCurve(lastPoint, c1, point)
                }
                'C' -> withPathContext(absolute, lastPoint) { readPoint ->
                    // Cubic bezier curve
                    val c1 = readPoint()
                    val c2 = readPoint()
                    point = readPoint()
                    addCurve(lastPoint, c1, c2, point)
                }
                'S' -> withPathContext(absolute, lastPoint) { readPoint ->
                    // Shorthand cubic bezier curve
                    val c1 = getShorthandControl(curves.last, 4, lastPoint)
                    val c2 = readPoint()
                    point = readPoint()
                    addCurve(lastPoint, c1, c2, point)
                }
                'A' -> withPathContext(absolute, lastPoint) { readPoint ->
                    val radius = this@PathParser.readPoint()
                    val rotation = Math.toRadians(readValue())
                    val largeArc = readBoolean()
                    val sweep = readBoolean()
                    point = readPoint()
                    curves += arcToCurves(lastPoint, point, radius, rotation, largeArc, sweep)
                }
                else -> error("Unknown command")  // Should never happen.
            }

            lastPoint = point
        }

        return Path(LinkedList(curves))
    }

    private fun addCurve(vararg points: Vec2) {
        if (points.first() != points.last()) {
            // Don't add element if start point matches end point which means element is invisible.
            curves += points.toList()
        }
    }

    private inline fun withPathContext(absolute: Boolean, lastPoint: Vec2,
                                       block: (readPoint: () -> Vec2) -> Unit) =
            if (absolute) {
                block { readPoint() }
            } else {
                block { readRelativePoint(lastPoint) }
            }

    private inline fun withPathContext(absolute: Boolean, lastValue: Double,
                                       block: (readValue: () -> Double) -> Unit) =
            if (absolute) {
                block { readValue() }
            } else {
                block { readValue() + lastValue }
            }

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

    private fun getShorthandControl(last: Curve, size: Int, currentPoint: Vec2) =
            if (last.size == size) {
                // The control point is the reflection of the second control point on
                // the previous cubic command relative to the current point.
                currentPoint + (currentPoint - last[last.size - 2])
            } else {
                // Assume control point is coincident with the current point.
                currentPoint
            }

}


