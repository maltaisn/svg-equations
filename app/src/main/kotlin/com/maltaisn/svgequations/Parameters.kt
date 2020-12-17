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

    @Parameter(names = ["-s", "--style"],
        description = "Whether to generate a Desmos-specific script for setting stroke style.",
        order = 5)
    var style = false

    @Parameter(names = ["-e", "--equations"],
        description = "Type of equations to generate, either 'parametric' or 'cartesian'.",
        order = 10)
    var type = TYPE_PARAMETRIC

    @Parameter(names = ["-t", "--transform"], description = "Transformations to apply on SVG.", order = 20)
    var transform = ""

    @Parameter(names = ["-x", "--latex"], description = "Whether to format output as latex equations.", order = 50)
    var convertToLatex = false

    @Parameter(names = ["-l", "--lenient"], description = "Enable lenient mode to ignore non-fatal errors.", order = 60)
    var lenient = false

    @Parameter(names = ["-h", "--help"], description = "Show help message.", help = true, order = 70)
    var help = false


    fun validate() {
        if (precision !in 0..8) {
            paramError("Precision must be between 0 and 8.")
        }
    }

    companion object {
        const val TYPE_PARAMETRIC = "parametric"
        const val TYPE_CARTESIAN = "cartesian"
    }
}
