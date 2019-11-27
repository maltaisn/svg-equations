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

package com.maltaisn.svgequations.parser

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals


internal class SvgParserTest {

    private val parser = SvgParser(false)

    @Test
    fun `parse input stream single path`() {
        val paths = parser.parse(javaClass.classLoader.getResourceAsStream("square-1.svg")!!)
        assertEquals(listOf("M0,0 v20 h20 v-20 Z"), paths)
    }

    @Test
    fun `parse input stream multiple paths`() {
        val paths = parser.parse(javaClass.classLoader.getResourceAsStream("square-3.svg")!!)
        assertEquals(listOf(
                "M0,0 h5 v5 h-5 Z",
                "M5,5 h10 v10 h-10 Z",
                "M15,15 h20 v20 h-20 Z"
        ), paths)
    }

    @Test(expected = SvgParseException::class)
    fun `parse file that does not exists`() {
        parser.parse(File("file.svg"))
    }

    @Test(expected = SvgParseException::class)
    fun `parse file wrong extension`() {
        parser.parse(File("file.kt"))
    }

}
