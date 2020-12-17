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
import com.maltaisn.svgequations.math.Vec2
import com.maltaisn.svgequations.math.binomialCoeff


/**
 * Equation generator used to generate parametric equations in the form `(x expression, y expression)`.
 * @param parameter Symbol used for parametric variable than varies between 0 and 1.
 */
class ParametricGenerator(override var formatter: EquationFormatter,
                          override val convertToLatex: Boolean = false,
                          val parameter: Char = 't') : EquationGenerator {

    override fun generateEquation(path: Path): List<String> {
        val equations = mutableListOf<String>()
        for (curve in path.curves) {
            // Get curve terms and coefficients from bezier definition
            val terms = mutableListOf<Pair<Vec2, String>>()
            val n = curve.size - 1
            for ((i, p) in curve.withIndex()) {
                // Using https://en.wikipedia.org/wiki/B%C3%A9zier_curve#General_definition
                val coeff = binomialCoeff(n, i)
                val t1 = raiseTermToPower("t", i)
                val t2 = raiseTermToPower("(1-t)", n - i)
                terms += (p * coeff) to (t1 + t2)
            }

            // Create equation for X and Y separatedly
            val xEq = formatter.format(terms.map { (p, t) -> p.x to t })
            val yEq = formatter.format(terms.map { (p, t) -> p.y to t })
            var equation = "($xEq, $yEq)"

            // Convert to latex if needed
            if (convertToLatex) {
                equation = convertToLatex(equation)
            }

            equations += equation
        }
        return equations
    }

    private fun raiseTermToPower(term: String, p: Int) = when (p) {
        0 -> ""
        1 -> term
        else -> "$term^$p"
    }

}
