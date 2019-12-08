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

import com.maltaisn.svgequations.math.Mat33
import org.junit.Test
import kotlin.test.assertEquals


internal class TransformParserTest {

    private val parser = TransformParser(false)
    private val parserLenient = TransformParser(true)

    @Test
    fun `translate x only`() {
        val t = parser.parse("translate(10)")
        assertEquals(Mat33.translate(10.0, 0.0), t)
    }

    @Test
    fun `translate x y`() {
        val t = parser.parse("translate(10,-10)")
        assertEquals(Mat33.translate(10.0, -10.0), t)
    }

    @Test
    fun `scale x only`() {
        val t = parser.parse("scale(10)")
        assertEquals(Mat33.scale(10.0, 10.0), t)
    }

    @Test
    fun `scale x y`() {
        val t = parser.parse("scale(10,-10)")
        assertEquals(Mat33.scale(10.0, -10.0), t)
    }

    @Test
    fun `rotate origin`() {
        val t = parser.parse("rotate(30)")
        assertEquals(Mat33.rotation(Math.toRadians(30.0)), t)
    }

    @Test
    fun `rotate on point`() {
        val t = parser.parse("rotate(30,10,10)")
        assertEquals(Mat33.translate(10.0, 10.0) *
                Mat33.rotation(Math.toRadians(30.0)) * Mat33.translate(-10.0, -10.0), t)
    }

    @Test
    fun `skew x`() {
        val t = parser.parse("skewX(30)")
        assertEquals(Mat33.skew(Math.toRadians(30.0), 0.0), t)
    }

    @Test
    fun `skew y`() {
        val t = parser.parse("skewY(30)")
        assertEquals(Mat33.skew(0.0, Math.toRadians(30.0)), t)
    }

}
