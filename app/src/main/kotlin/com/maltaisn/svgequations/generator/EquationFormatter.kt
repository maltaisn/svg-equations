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

package com.maltaisn.svgequations.generator

import java.text.NumberFormat
import kotlin.math.absoluteValue


class EquationFormatter(
        val numberFmt: NumberFormat = NumberFormat.getNumberInstance()) {

    /**
     * Format an [equation] map, made of coefficient-term pairs.
     * Terms with coefficient of 0 are omitted. Coefficient of 1 are not shown.
     * If resulting equation is empty, `0` is returned.
     */
    fun format(equation: List<Pair<Double, String>>,
               boundsSymbol: Char = 'x',
               boundsStart: Double = Double.NEGATIVE_INFINITY,
               boundsEnd: Double = Double.POSITIVE_INFINITY): String {
        require(boundsEnd > boundsStart) { "End bound must be greater than start bound." }

        val sb = StringBuilder()
        for ((coeff, term) in equation) {
            val coeffStr = numberFmt.format(coeff.absoluteValue)

            // If coefficient is 0, skip this term.
            if (coeffStr == "0") continue

            // Append sign
            if (sb.isEmpty()) {
                if (coeff < 0) {
                    sb.append('-')
                }
            } else {
                sb.append(' ')
                sb.append(if (coeff > 0) '+' else '-')
                sb.append(' ')
            }

            // Append coefficient if not 1.
            if (coeffStr != "1") {
                sb.append(coeffStr)
            }

            // Append term
            sb.append(term)
        }

        // Append bounds
        if (boundsStart != Double.NEGATIVE_INFINITY || boundsEnd != Double.POSITIVE_INFINITY) {
            sb.append(" {")
            when {
                boundsStart.isInfinite() -> {
                    // "x <= end"
                    sb.append(boundsSymbol)
                    sb.append(" <= ")
                    sb.append(numberFmt.format(boundsEnd))
                }
                boundsEnd.isInfinite() -> {
                    // "x >= start"
                    sb.append(boundsSymbol)
                    sb.append(" >= ")
                    sb.append(numberFmt.format(boundsStart))
                }
                else -> {
                    // "start <= x <= end"
                    sb.append(numberFmt.format(boundsStart))
                    sb.append(" <= ")
                    sb.append(boundsSymbol)
                    sb.append(" <= ")
                    sb.append(numberFmt.format(boundsEnd))
                }
            }
            sb.append("}")
        }

        if (sb.isEmpty()) {
            // Empty equation, append 0 constant.
            sb.append('0')
        }

        return sb.toString()
    }

}
