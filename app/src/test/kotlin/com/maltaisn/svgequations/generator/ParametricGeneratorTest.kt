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

import com.maltaisn.svgequations.Color
import com.maltaisn.svgequations.Path
import com.maltaisn.svgequations.math.Vec2
import org.junit.Test
import kotlin.test.assertEquals


internal class ParametricGeneratorTest {

    private val generator = ParametricGenerator(EquationFormatter())
    private val latexGenerator = ParametricGenerator(EquationFormatter(), true)

    @Test
    fun `generate line path equation`() {
        val path = Path(listOf(listOf(Vec2(1.0, 2.0), Vec2(10.0, 5.0))), Color.BLACK)
        val equation = generator.generateEquation(path)
        assertEquals(listOf("((1-t) + 10t, 2(1-t) + 5t)"), equation)
    }

    @Test
    fun `generate quadratic curve path equation`() {
        val path = Path(listOf(listOf(Vec2(1.0, 2.0), Vec2(3.0, 4.0), Vec2(10.0, 5.0))), Color.BLACK)
        val equation = generator.generateEquation(path)
        assertEquals(listOf("((1-t)^2 + 6t(1-t) + 10t^2, 2(1-t)^2 + 8t(1-t) + 5t^2)"), equation)
    }

    @Test
    fun `generate cubic curve path equation`() {
        val path = Path(listOf(listOf(Vec2(1.0, 2.0), Vec2(3.0, 4.0), Vec2(5.0, 6.0), Vec2(10.0, 5.0))), Color.BLACK)
        val equation = generator.generateEquation(path)
        assertEquals(listOf("((1-t)^3 + 9t(1-t)^2 + 15t^2(1-t) + 10t^3, " +
                "2(1-t)^3 + 12t(1-t)^2 + 18t^2(1-t) + 5t^3)"), equation)
    }

}
