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
import java.io.File


class Parameters {

    @Parameter
    private val files = mutableListOf<String>()

    @Parameter(names = ["-p", "--precision"], description = "Precision of the generated equations", order = 0)
    private val precision = 2

    @Parameter(names = ["-s", "--scale"], description = "Scale factor of SVG paths", order = 1)
    private val scale = 1.0

    @Parameter(names = ["-a", "--angle-units"], description = "Angle units, either 'radians' or 'degrees'", order = 2)
    var angleUnits = ANGLE_DEGREES

    @Parameter(names = ["-l", "--lenient"], description = "Enable lenient mode to ignore non-fatal errors.", order = 3)
    var lenient = false

    @Parameter(names = ["-h", "--help"], description = "Show help message", help = true, order = 4)
    var help = false


    fun validate() {
        if (precision !in 0..8) {
            paramError("Precision must be between 0 and 8.")
        }

        if (scale <= 0.0) {
            paramError("Scale must be greater than 0.")
        }

        if (angleUnits != ANGLE_DEGREES && angleUnits != ANGLE_RADIANS) {
            paramError("Invalid angle units.")
        }

        for (fileName in files) {
            if (!File(fileName).exists()) {
                paramError("Input file '$fileName' doesn't exist.")
            }
        }
    }

    companion object {
        val ANGLE_DEGREES = "degrees"
        val ANGLE_RADIANS = "radians"
    }

}
