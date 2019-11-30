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


interface EquationGenerator {

    /**
     * Number format to use for formatting equations numbers.
     */
    var formatter: EquationFormatter

    /**
     * Whether to convert output to latex format or not.
     * The [convertToLatex] function is used for that.
     */
    val convertToLatex: Boolean

    /**
     * Generate a list of equations from a SVG [path].
     */
    fun generateEquation(path: Path): List<String>

    /**
     * Replace some symbols in an [equation] with their latex equivalent.
     */
    fun convertToLatex(equation: String) = equation
            .replace("(", "\\left(")
            .replace(")", "\\right)")
            .replace("{", "\\left{")
            .replace("}", "\\right}")

}
