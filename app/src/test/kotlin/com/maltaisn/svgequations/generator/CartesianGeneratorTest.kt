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
import com.maltaisn.svgequations.ParameterException
import com.maltaisn.svgequations.Path
import com.maltaisn.svgequations.math.Vec2
import org.junit.Test
import kotlin.test.assertEquals


internal class CartesianGeneratorTest {

    private val generator = CartesianGenerator(EquationFormatter())

    @Test
    fun `generate line path equation`() {
        val path = Path(listOf(listOf(Vec2(1.0, 1.0), Vec2(11.0, 6.0))), Color.BLACK, 1.0)
        val equation = generator.generateEquation(path)
        assertEquals(listOf("-5x + 10y = 5 {1 <= x <= 11}"), equation)
    }

    @Test(expected = ParameterException::class)
    fun `fail for curve`() {
        val path = Path(listOf(listOf(Vec2(1.0, 1.0), Vec2(2.0, 2.0), Vec2(3.0, 1.0))), Color.BLACK, 1.0)
        generator.generateEquation(path)
    }

}
