/*
 * Copyright 2020 Nicolas Maltais
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
import java.util.Base64

/**
 * Class used to generate a Desmos-specific script to set stroke styles for each
 * generated equations, from the SVG path styles.
 */
class StyleGenerator {

    /**
     * Returns a string containing the styling script for [paths].
     */
    fun generateStyleScript(paths: List<Path>) = """
            n = ${paths.sumBy { it.curves.size }}
            data = atob("${createStyleDataString(paths)}");
            
            state = Calc.getState();
            j = 0;
            for (i = 0; i < n; i++) {
                eq = state.expressions.list[i]
                eq.lineOpacity = (data.charCodeAt(j++) / 255).toFixed(2)
                eq.color = "#" + (data.charCodeAt(j++) | data.charCodeAt(j++) << 8 | 
                                   data.charCodeAt(j++) << 16).toString(16).padStart(6, "0");
            }
            Calc.setState(state)
        """.trimIndent()

    private fun createStyleDataString(paths: List<Path>): String {
        val colorArray = ByteArray(4 * paths.sumBy { it.curves.size })
        var i = 0
        for (path in paths) {
            repeat(path.curves.size) {
                colorArray[i] = path.color.a.toByte()
                colorArray[i + 1] = path.color.r.toByte()
                colorArray[i + 2] = path.color.g.toByte()
                colorArray[i + 3] = path.color.b.toByte()
                i += 4
            }
        }
        return Base64.getEncoder().encodeToString(colorArray)
    }

}