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

package com.maltaisn.svgequations.parser

import com.maltaisn.svgequations.Color
import org.junit.Test
import kotlin.test.assertEquals

class ColorParserTest {

    private val parser = ColorParser(false)
    private val parserLenient = ColorParser(true)

    @Test
    fun `should parse hex color without alpha`() {
        assertEquals(Color(0, 0, 0), parser.parse("#000000", null))
        assertEquals(Color(255, 127, 127), parser.parse("#FF7F7F", null))
        assertEquals(Color(9, 138, 31), parser.parse("#098a1f", null))
        assertEquals(Color(255, 255, 255), parser.parse("#ffffff", null))
    }

    @Test
    fun `should parse hex color with alpha (ignored)`() {
        assertEquals(Color(0, 0, 0), parser.parse("#88000000", null))
        assertEquals(Color(255, 127, 127), parser.parse("#00FF7F7F", null))
        assertEquals(Color(9, 138, 31), parser.parse("#9a098a1f", null))
        assertEquals(Color(255, 255, 255), parser.parse("#18ffffff", null))
    }

    @Test
    fun `should parse short hex color without alpha`() {
        assertEquals(Color(0, 0, 0), parser.parse("#000", null))
        assertEquals(Color(255, 136, 136), parser.parse("#f88", null))
        assertEquals(Color(17, 153, 34), parser.parse("#192", null))
        assertEquals(Color(255, 255, 255), parser.parse("#fff", null))
    }

    @Test
    fun `should parse short hex color with alpha (ignored)`() {
        assertEquals(Color(0, 0, 0), parser.parse("#8000", null))
        assertEquals(Color(255, 136, 136), parser.parse("#0f88", null))
        assertEquals(Color(17, 153, 34), parser.parse("#9192", null))
        assertEquals(Color(255, 255, 255), parser.parse("#ffff", null))
    }

    @Test
    fun `should parse named color`() {
        assertEquals(Color(0, 0, 0), parser.parse("black", null))
        assertEquals(Color(255, 0, 0), parser.parse("red", null))
        assertEquals(Color(255, 255, 0), parser.parse("yellow", null))
        assertEquals(Color(0, 255, 0), parser.parse("lime", null))
    }

    @Test
    fun `should parse rgb color`() {
        assertEquals(Color(0, 1, 2), parser.parse("rgb(0 1 2)", null))
        assertEquals(Color(3, 35, 99), parser.parse("rgb(2.5, 35, 99)", null))
        assertEquals(Color(255, 128, 64), parser.parse("rgb(100%, 50%, 25%)", null))
        assertEquals(Color(255, 25, 192), parser.parse("rgb(100% 25 192)", null))
    }

    @Test
    fun `should parse rgba color`() {
        assertEquals(Color(0, 1, 2), parser.parse("rgba(0 1 2 50)", null))
        assertEquals(Color(3, 35, 99), parser.parse("rgba(2.5, 35, 99, 10%)", null))
        assertEquals(Color(255, 128, 64), parser.parse("rgba(100%, 50%, 25% / 0.12)", null))
        assertEquals(Color(255, 25, 192), parser.parse("rgba(100% 25 192 / 19%)", null))
    }

    @Test
    fun `should parse opacity`() {
        assertEquals(Color(0, 0, 0, 255), parser.parse(null, "100%"))
        assertEquals(Color(0, 0, 0, 0), parser.parse(null, "0.00%"))
        assertEquals(Color(0, 0, 0, 128), parser.parse(null, "0.5"))
        assertEquals(Color(0, 0, 0, 128), parser.parse(null, "50%"))
    }
}