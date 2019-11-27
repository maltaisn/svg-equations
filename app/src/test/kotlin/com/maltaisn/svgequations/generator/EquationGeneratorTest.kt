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

import com.maltaisn.svgequations.element.Path
import org.junit.Test
import kotlin.test.assertEquals


internal class EquationGeneratorTest {

    @Test
    fun `latex formatter`() {
        val generator = TestGenerator(EquationFormatter(), true)
        assertEquals("2\\left(a - 1\\right) \\left{-10 <= a <= 10\\right}",
                generator.convertToLatex("2(a - 1) {-10 <= a <= 10}"))
    }

    private class TestGenerator(override var formatter: EquationFormatter,
                                override val convertToLatex: Boolean) : EquationGenerator {
        override fun generateEquation(path: Path) = emptyList<String>()
    }

}
