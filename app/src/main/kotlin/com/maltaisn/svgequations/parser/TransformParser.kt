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

import com.maltaisn.svgequations.math.Mat33


/**
 * Class used
 *
 * @param lenient If lenient, parser will ignore non-fatal errors.
 */
class TransformParser(val lenient: Boolean) {

    private val tokenizer = PathTokenizer(lenient)

    /**
     * Parse SVG path [transformStr] string and return the transformation matrix.
     * @throws SvgParseException Thrown on parse errors.
     */
    fun parse(transformStr: String): Mat33 {
        var i = 0
        var transform = Mat33.IDENTITY
        while (i < transformStr.length) {
            val c = transformStr[i]
            if (c == ',' || c.isWhitespace()) {
                i++
                continue
            }

            val funcEnd = transformStr.indexOf('(', i)
            val func = transformStr.substring(i, funcEnd)
            val valuesEnd = transformStr.indexOf(')', funcEnd)
            val v = tokenizer.parseValues(transformStr.substring(funcEnd + 1, valuesEnd))

            // Check number of values
            val arity = FUNCTIONS_ARITY[func] ?: parseError("Unknown transform function '$func'", i)
            if (v.size < arity.first()) {
                parseError("Not enough values for transform function '$func'", i)
            } else if (!lenient) {
                if (v.size > arity.last()) {
                    parseError("Too many values for transform function '$func'", i)
                } else if (v.size !in arity) {
                    parseError("Wrong number of values for transform function '$func'", i)
                }
            }

            // Apply transform
            transform *= when (func) {
                "matrix" -> Mat33(v[0], v[2], v[4], v[1], v[3], v[5], 0.0, 0.0, 1.0)
                "translate" -> Mat33.translate(v[0], v.getOrNull(1) ?: 0.0)
                "scale" -> Mat33.scale(v[0], v.getOrNull(1) ?: v[0])
                "skewX" -> Mat33.skew(Math.toRadians(v[0]), 0.0)
                "skewY" -> Mat33.skew(0.0, Math.toRadians(v[0]))
                "rotate" -> {
                    val rot = Mat33.rotation(Math.toRadians(v[0]))
                    if (v.size >= 3) {
                        Mat33.translate(v[1], v[2]) * rot * Mat33.translate(-v[1], -v[2])
                    } else {
                        rot
                    }
                }
                else -> Mat33.IDENTITY
            }

            i = valuesEnd + 1
        }

        return transform
    }

    companion object {
        private val FUNCTIONS_ARITY = mapOf(
                "matrix" to intArrayOf(6),
                "translate" to intArrayOf(1, 2),
                "scale" to intArrayOf(1, 2),
                "rotate" to intArrayOf(1, 3),
                "skewX" to intArrayOf(1),
                "skewY" to intArrayOf(1))
    }

}
