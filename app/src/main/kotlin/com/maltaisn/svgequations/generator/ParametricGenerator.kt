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

import com.maltaisn.svgequations.element.*


/**
 * Equation generator used to generate parametric equations in the form `(x expression, y expression)`.
 * @param parameter Symbol used for parametric variable than varies between 0 and 1.
 */
class ParametricGenerator(override var formatter: EquationFormatter,
                          override val convertToLatex: Boolean = false,
                          val parameter: Char = 't') : EquationGenerator {

    override fun generateEquation(path: Path): List<String> {
        val equations = mutableListOf<String>()
        loop@ for (element in path.elements) {
            var equation = when (element) {
                is Line -> getLineEquation(element)
                is Curve -> getCurveEquation(element)
                is Arc -> getArcEquation(element)
                else -> continue@loop
            }
            if (convertToLatex) {
                equation = convertToLatex(equation)
            }
            equations += equation
        }
        return equations
    }

    private fun getLineEquation(line: Line): String {
        // Line: P0 * (1 - t) + P1 * t
        val t = parameter
        return getParametricEquation(
                line.start to "(1-$t)",
                line.end to "$t")
    }

    private fun getCurveEquation(curve: Curve): String {
        val t = parameter
        return when (curve.controls.size) {
            0 -> getLineEquation(Line(curve.start, curve.end))
            1 -> {
                // Quadratic: P0 * (1-t)^2 + P1 * 2t(1-t) + P2 * t^2
                getParametricEquation(
                        curve.start to "(1-$t)^2",
                        (curve.controls[0] * 2) to "$t(1-$t)",
                        curve.end to "$t^2")
            }
            2 -> {
                // Cubic: P0 * (1-t)^3 + P1 * 3t(1-t)^2 + P2 * 3t^2(1-t) + P3 * t^3
                getParametricEquation(
                        curve.start to "(1-$t)^3",
                        (curve.controls[0] * 3) to "$t(1-$t)^2",
                        (curve.controls[1] * 3) to "$t^2(1-$t)",
                        curve.end to "$t^3")
            }
            else -> throw UnsupportedOperationException("Unsupported bezier order.")
        }
    }

    private fun getArcEquation(arc: Arc): String {
        // Ellipse:
        // x = cx + rx * cos(t) * cos(r) - ry * sin(t) * sin(r)
        // y = cy + rx * cos(t) * sin(r) - ry * sin(t) * cos(r)

        TODO()
    }

    private fun getParametricEquation(vararg terms: Pair<Vec2, String>): String {
        val xEq = formatter.format(terms.map { (p, t) -> p.x to t })
        val yEq = formatter.format(terms.map { (p, t) -> p.y to t })
        return "($xEq, $yEq)"
    }

}
