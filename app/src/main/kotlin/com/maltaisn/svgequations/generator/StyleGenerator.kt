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
import org.intellij.lang.annotations.Language
import java.util.Base64
import kotlin.math.roundToInt

/**
 * Class used to generate a Desmos-specific script to set stroke styles for each
 * generated equations, from the SVG path styles.
 */
class StyleGenerator {

    /**
     * Returns a string containing the styling script for [paths].
     */
    @Language("JavaScript")
    fun generateStyleScript(paths: List<Path>) =
        """
            var d = atob("${createStyleDataString(paths)}");
            var dc = d.charCodeAt.bind(d);
            var s = Calc.getState();
            for (i = 0, j = 0; i < ${paths.sumBy { it.curves.size }}; i++) {
                var e = s.expressions.list[i];
                e.lineOpacity = (dc(j++) / 255).toFixed(2);
                e.color = "#" + (dc(j++) << 16 | dc(j++) << 8 | dc(j++)).toString(16).padStart(6, "0");
                e.lineWidth = (dc(j++) / 10).toFixed(1);
            }
            Calc.setState(s);
        """.trimIndent()

    private fun createStyleDataString(paths: List<Path>): String {
        val colorArray = ByteArray(5 * paths.sumBy { it.curves.size })
        var i = 0
        for (path in paths) {
            repeat(path.curves.size) {
                colorArray[i] = path.color.a.toByte()
                colorArray[i + 1] = path.color.r.toByte()
                colorArray[i + 2] = path.color.g.toByte()
                colorArray[i + 3] = path.color.b.toByte()
                colorArray[i + 4] = (path.width * 10).roundToInt().coerceIn(0, 255).toByte()
                i += 5
            }
        }
        return Base64.getEncoder().encodeToString(colorArray)
    }

}