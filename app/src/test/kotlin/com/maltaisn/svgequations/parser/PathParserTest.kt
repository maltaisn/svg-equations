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

import com.maltaisn.svgequations.Curve
import com.maltaisn.svgequations.math.Vec2
import org.junit.Assert
import org.junit.Test
import kotlin.math.pow
import kotlin.test.assertEquals


internal class PathParserTest {

    private val parser = PathParser(false)
    private val parserLenient = PathParser(true)

    private val tokenizer = PathTokenizer(false)

    @Test
    fun `moveTo implicit`() {
        val path = parser.parse(tokenizer.tokenize("L20,20"))
        assertEquals(listOf(
            listOf(Vec2(0.0, 0.0), Vec2(20.0, 20.0))
        ), path)
    }

    @Test
    fun `lineTo absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 L20,20"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0))
        ), path)
    }

    @Test
    fun `lineTo relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 l10,10"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0))
        ), path)
    }

    @Test
    fun `lineTo horizontal absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 H20"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 10.0))
        ), path)
    }

    @Test
    fun `lineTo horizontal relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 h10"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 10.0))
        ), path)
    }

    @Test
    fun `lineTo vertical absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 V20"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(10.0, 20.0))
        ), path)
    }

    @Test
    fun `lineTo vertical relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 v10"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(10.0, 20.0))
        ), path)
    }

    @Test
    fun `lineTo zero width`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 L10,10"))
        assertEquals(emptyList(), path)
    }

    @Test
    fun `closePath triangle`() {
        val path = parser.parse(tokenizer.tokenize("M0,0 L10,0 L0,10 Z"))
        assertEquals(listOf(
            listOf(Vec2(0.0, 0.0), Vec2(10.0, 0.0)),
            listOf(Vec2(10.0, 0.0), Vec2(0.0, 10.0)),
            listOf(Vec2(0.0, 10.0), Vec2(0.0, 0.0))
        ), path)
    }

    @Test
    fun `closePath empty`() {
        val path = parser.parse(tokenizer.tokenize("M0,0 Z"))
        assertEquals(emptyList(), path)
    }

    @Test
    fun `quadratic absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 Q20,20 30,10"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 10.0))
        ), path)
    }

    @Test
    fun `quadratic relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 q10,10 20,0"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 10.0))
        ), path)
    }

    @Test
    fun `quadratic shorthand absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 Q20,20 30,10 T50,10"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 10.0)),
            listOf(Vec2(30.0, 10.0), Vec2(40.0, 0.0), Vec2(50.0, 10.0))
        ), path)
    }

    @Test
    fun `quadratic shorthand relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 Q20,20 30,10 t20,0"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 10.0)),
            listOf(Vec2(30.0, 10.0), Vec2(40.0, 0.0), Vec2(50.0, 10.0))
        ), path)
    }

    @Test
    fun `cubic absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 C20,20 30,20 40,10"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 20.0), Vec2(40.0, 10.0))
        ), path)
    }

    @Test
    fun `cubic relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 c10,10 20,10 30,0"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 20.0), Vec2(40.0, 10.0))
        ), path)
    }

    @Test
    fun `cubic shorthand absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 C20,20 30,20 40,10 S60,0 70,10"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 20.0), Vec2(40.0, 10.0)),
            listOf(Vec2(40.0, 10.0), Vec2(50.0, 0.0), Vec2(60.0, 0.0), Vec2(70.0, 10.0))
        ), path)
    }

    @Test
    fun `cubic shorthand relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 C20,20 30,20 40,10 s20,-10 30,0"))
        assertEquals(listOf(
            listOf(Vec2(10.0, 10.0), Vec2(20.0, 20.0), Vec2(30.0, 20.0), Vec2(40.0, 10.0)),
            listOf(Vec2(40.0, 10.0), Vec2(50.0, 0.0), Vec2(60.0, 0.0), Vec2(70.0, 10.0))
        ), path)
    }

    // Following arc tests were taken from: https://github.com/fontello/svgpath/blob/0fd9c7e7eba1b735142d7d3e567b8a607130a8a5/test/api.js#L459
    // These tests required that previous tests for parsing curves work since expected output is parsed.
    @Test
    fun `arc transform to 4 curves`() {
        val actual = parser.parse(tokenizer.tokenize("M100 100 A30 50 0 1 1 110 110"))
        val expected = parser.parse(tokenizer.tokenize("M100 100C89 83 87 54 96 33 105 12 122 7 136 20 149 33 154 61 147 84 141 108 125 119 110 110"))
        assertCurvesEquals(expected, actual)
    }

    @Test
    fun `arc transform to circle`() {
        val actual = parser.parse(tokenizer.tokenize("M 100, 100 m -75, 0 a 75,75 0 1,0 150,0 a 75,75 0 1,0 -150,0"))
        val expected = parser.parse(tokenizer.tokenize("M100 100m-75 0C25 141 59 175 100 175 141 175 175 141 175 100 175 59 141 25 100 25 59 25 25 59 25 100"))
        assertCurvesEquals(expected, actual)
    }

    @Test
    fun `arc transform rounding error`() {
        val actual = parser.parse(tokenizer.tokenize("M-0.5 0 A 0.09188163040671497 0.011583783896639943 0 0 1 0 0.5"))
        val expected = parser.parse(tokenizer.tokenize("M-0.5 0C0.59517-0.01741 1.59491 0.08041 1.73298 0.21848 1.87105 0.35655 1.09517 0.48259 0 0.5"))
        assertCurvesEquals(expected, actual)
    }

    @Test
    fun `arc start equals end`() {
        val actual = parser.parse(tokenizer.tokenize("M100 100A123 456 90 0 1 100 100"))
        val expected = parser.parse(tokenizer.tokenize("M100 100L100 100"))
        assertCurvesEquals(expected, actual)
    }

    @Test
    fun `arc zero radii`() {
        val actual = parser.parse(tokenizer.tokenize("M100 100A0 0 0 0 1 110 110"))
        val expected = parser.parse(tokenizer.tokenize("M100 100L110 110"))
        assertCurvesEquals(expected, actual)
    }

    private fun assertCurvesEquals(expected: List<Curve>, actual: List<Curve>, precision: Int = 0) {
        assertEquals(expected.size, actual.size)
        val delta = 10.0.pow(-precision) / 2
        for ((expCurve, actCurve) in expected.zip(actual)) {
            for ((exp, act) in expCurve.zip(actCurve)) {
                Assert.assertEquals(exp.x, act.x, delta)
                Assert.assertEquals(exp.y, act.y, delta)
            }
        }
    }

    @Test(expected = SvgParseException::class)
    fun `arc bad boolean fail`() {
        parser.parse(tokenizer.tokenize("M10,10 a20,10 90 1223,1 20,0"))
    }

}
