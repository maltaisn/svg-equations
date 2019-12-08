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

import com.maltaisn.svgequations.Path
import com.maltaisn.svgequations.paramError
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


/**
 * Equation generator used to generate cartesian equations.
 * Note: cartesian equations usually require a lot more precision than parametric equations
 * to achieve the same result.
 */
class CartesianGenerator(override var formatter: EquationFormatter,
                         override val convertToLatex: Boolean = false) : EquationGenerator {

    override fun generateEquation(path: Path): List<String> {
        val equations = mutableListOf<String>()
        for (p in path.curves) {
            var equation: String = when (p.size) {
                2 -> {
                    val x = p[0].y - p[1].y
                    val y = -p[0].x + p[1].x
                    val c = -p[0].x * p[1].y + p[1].x * p[0].y

                    val dx = (p[1].x - p[0].x).absoluteValue
                    val dy = (p[1].y - p[0].y).absoluteValue
                    if (dx > dy) {
                        formatter.format(listOf(x to "x", y to "y"), c,
                                'x', min(p[0].x, p[1].x), max(p[0].x, p[1].x))
                    } else {
                        formatter.format(listOf(x to "x", y to "y"), c,
                                'y', min(p[0].y, p[1].y), max(p[0].y, p[1].y))
                    }
                }
                else -> {
                    // Bezier to cartesian is actually possible and can be approximated
                    // quickly by fitting a polynomial of the same degree, but it wouldn't
                    // be easy to determine the bounds of the polynomial. Specifying x/y
                    // bounds isn't enough since there can be loops in the curve.
                    paramError("Cartesian equation type doesn't support curves.")
                }
            }

            // Convert to latex if needed
            if (convertToLatex) {
                equation = convertToLatex(equation)
            }

            equations += equation
        }
        return equations
    }

}
