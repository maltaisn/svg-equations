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

    @Parameter(names = ["-p", "--precision"], description = "Precision of the generated equations")
    private val precision = 2

    @Parameter(names = ["-s", "--scale"], description = "Scale factor of SVG paths")
    private val scale = 1.0

    @Parameter(names = ["-h", "--help"], description = "Show help message", help = true)
    var help = false


    fun validate() {
        if (precision !in 0..8) {
            paramError("Precision must be between 0 and 8.")
        }

        for (fileName in files) {
            if (!File(fileName).exists()) {
                paramError("Input file '$fileName' doesn't exist.")
            }
        }
    }


}
