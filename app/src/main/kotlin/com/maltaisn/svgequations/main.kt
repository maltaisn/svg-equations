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

package com.maltaisn.svgequations

import com.beust.jcommander.JCommander
import com.maltaisn.svgequations.generator.CartesianGenerator
import com.maltaisn.svgequations.generator.EquationFormatter
import com.maltaisn.svgequations.generator.EquationGenerator
import com.maltaisn.svgequations.generator.ParametricGenerator
import com.maltaisn.svgequations.math.Mat33
import com.maltaisn.svgequations.parser.PathParser
import com.maltaisn.svgequations.parser.PathTokenizer
import com.maltaisn.svgequations.parser.SvgParser
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val params = Parameters()
    val commander = JCommander.newBuilder().addObject(params).build()
    commander.programName = "svg-equations"

    try {
        // Parse arguments
        try {
            commander.parse(*args)
        } catch (e: com.beust.jcommander.ParameterException) {
            paramError(e.message)
        }

        if (params.help) {
            // Show help message
            commander.usage()
            exitProcess(1)
        }

        // Validate arguments
        params.validate()

        // Create parsers
        val svgParser = SvgParser(params.lenient)
        val pathTokenizer = PathTokenizer(params.lenient)
        val pathParser = PathParser(params.lenient)

        // Create equation generator
        val formatter = EquationFormatter(DecimalFormat().apply {
            decimalFormatSymbols = DecimalFormatSymbols().apply {
                decimalSeparator = '.'
            }
            maximumFractionDigits = params.precision
            groupingSize = 0
        })
        val generator: EquationGenerator = when (params.type) {
            Parameters.TYPE_PARAMETRIC -> ParametricGenerator(formatter, params.convertToLatex)
            Parameters.TYPE_CARTESIAN -> CartesianGenerator(formatter, params.convertToLatex)
            else -> error("Unknown type")
        }

        // Create transformation matrix
        val transform = Mat33.scale(params.scale[0], params.scale[1]) *
                Mat33.translate(params.translate[0], params.translate[1]) *
                Mat33.rotation(Math.toRadians(params.rotation))

        // Generate equations
        for (filename in params.files) {
            val file = File(filename)

            // Parse SVG paths and transform them
            val pathsData = svgParser.parse(file)
            val paths = pathsData.map { pathParser.parse(pathTokenizer.tokenize(it)).transform(transform) }

            // Generate and output equations
            val equations = paths.flatMap { generator.generateEquation(it) }
            val output = file.resolveSibling("${file.nameWithoutExtension}-output.txt")
            output.writeText(equations.joinToString("\n"))
        }

        exitProcess(0)

    } catch (e: Exception) {
        println("ERROR: ${e.message}\n")
        exitProcess(1)
    }

}
