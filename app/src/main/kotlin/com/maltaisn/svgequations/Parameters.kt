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

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters


@Parameters(separators = " =")
class Parameters {

    @Parameter
    var files = mutableListOf<String>()

    @Parameter(names = ["-p", "--precision"], description = "Precision of the generated equations.", order = 0)
    var precision = 2

    @Parameter(names = ["--scale"], arity = 2, description = "SVG scale factor in each direction (scaleX, scaleY).", order = 10)
    var scale: List<Double> = listOf(1.0, -1.0)

    @Parameter(names = ["--rotate"], description = "SVG rotation in degrees, counter-clockwise.", order = 20)
    var rotation: Double = 0.0

    @Parameter(names = ["--translate"], arity = 2, description = "SVG offset distance in pixels (offsetX, offsetY).", order = 30)
    var translate: List<Double> = listOf(0.0, 0.0)

    @Parameter(names = ["-a", "--angle-units"], description = "Angle units, either 'radians' or 'degrees'.", order = 40)
    var angleUnits = ANGLE_DEGREES

    @Parameter(names = ["-t", "--type"], description = "Type of equations to generate, either 'parametric' or 'cartesian'.", order = 50)
    var type = TYPE_PARAMETRIC

    @Parameter(names = ["-x", "--latex"], description = "Whether to format output as latex equations.", order = 60)
    var convertToLatex = false

    @Parameter(names = ["-l", "--lenient"], description = "Enable lenient mode to ignore non-fatal errors.", order = 70)
    var lenient = false

    @Parameter(names = ["-h", "--help"], description = "Show help message.", help = true, order = 80)
    var help = false


    fun validate() {
        if (precision !in 0..8) {
            paramError("Precision must be between 0 and 8.")
        }

        if (angleUnits != ANGLE_DEGREES && angleUnits != ANGLE_RADIANS) {
            paramError("Invalid angle units.")
        }
    }

    companion object {
        val ANGLE_DEGREES = "degrees"
        val ANGLE_RADIANS = "radians"

        val TYPE_PARAMETRIC = "parametric"
        val TYPE_CARTESIAN = "cartesian"
    }

}
