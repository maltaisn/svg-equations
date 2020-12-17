/*
 * Copyright 2020 Nicolas Maltais
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

import com.maltaisn.svgequations.Color
import kotlin.math.roundToInt

/**
 * Class used to parse CSS colors found in SVG files.
 * Only a subset of the specification is supported, namely hex colors, rgb, and rgba values.
 * A few common named colors are also supported.
 */
class ColorParser(val lenient: Boolean) {

    fun parse(color: String?, opacity: String?): Color {
        val alpha = if (opacity != null) {
            (if (opacity.endsWith('%')) {
                (opacity.substring(0, opacity.length - 1).toFloatOrNull() ?: return invalidColor(opacity)) / 100
            } else {
                opacity.toFloatOrNull() ?: return invalidColor(opacity)
            } * Color.COMPONENT_MAX).roundToInt()
        } else {
            Color.COMPONENT_MAX
        }
        return when {
            color == null -> Color.BLACK
            color.startsWith('#') -> parseHexColor(color)
            color.startsWith("rgb") -> parseRgbColor(color)
            else -> parseNamedColor(color)
        }.withAlpha(alpha)
    }

    private fun parseHexColor(color: String) = try {
        Color.hex(color)
    } catch (e: IllegalArgumentException) {
        invalidColor(color)
    }

    private fun parseRgbColor(color: String): Color {
        val parts = color.substringAfter('(').substringBefore(')').split(COLOR_PARTS_REGEX)
        val values = mutableListOf<Int>()
        for (part in parts) {
            if (part == "/") {
                if (values.size != 3) {
                    return invalidColor(color)
                }
                continue
            }
            val value = if (part.endsWith('%')) {
                (part.substring(0, part.length - 1).toFloatOrNull()
                    ?: return invalidColor(color)) / 100 * Color.COMPONENT_MAX
            } else {
                part.toFloatOrNull() ?: return invalidColor(color)
            }.roundToInt()
            if (value < 0) {
                return invalidColor(color)
            }
            values += value.coerceAtMost(Color.COMPONENT_MAX)
        }
        if (values.size !in 3..4) {
            return invalidColor(color)
        }
        return Color(values[0], values[1], values[2])
    }

    private fun parseNamedColor(color: String) =
        COLOR_NAME_MAP[color.toLowerCase()] ?: invalidColor(color)

    private fun invalidColor(color: String): Color {
        if (lenient) {
            return Color.BLACK
        }
        parseError("Invalid color string '$color'")
    }

    companion object {
        private val COLOR_PARTS_REGEX = """\h+""".toRegex()

        private val COLOR_NAME_MAP = mapOf(
            "black" to Color.hex("#000000"),
            "blue" to Color.hex("#0000ff"),
            "brown" to Color.hex("#a52a2a"),
            "cyan" to Color.hex("#00ffff"),
            "darkgray" to Color.hex("#a9a9a9"),
            "gray" to Color.hex("#808080"),
            "green" to Color.hex("#008000"),
            "lightgray" to Color.hex("#d3d3d3"),
            "lime" to Color.hex("#00ff00"),
            "magenta" to Color.hex("#ff00ff"),
            "orange" to Color.hex("#ffa500"),
            "pink" to Color.hex("#ffc0cb"),
            "purple" to Color.hex("#800080"),
            "red" to Color.hex("#ff0000"),
            "white" to Color.hex("#ffffff"),
            "yellow" to Color.hex("#ffff00"),
        )
    }

}