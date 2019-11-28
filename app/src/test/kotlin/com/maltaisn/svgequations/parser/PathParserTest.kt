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

import com.maltaisn.svgequations.element.Arc
import com.maltaisn.svgequations.element.Curve
import com.maltaisn.svgequations.element.Line
import com.maltaisn.svgequations.math.Vec2
import org.junit.Test
import kotlin.test.assertEquals


internal class PathParserTest {

    private val parser = PathParser(false)
    private val parserLenient = PathParser(true)

    private val tokenizer = PathTokenizer(false)

    @Test
    fun `moveTo implicit`() {
        val path = parser.parse(tokenizer.tokenize("L20,20"))
        assertEquals(listOf(
                Line(Vec2(0.0, 0.0), Vec2(20.0, 20.0))
        ), path.elements)
    }

    @Test
    fun `lineTo absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 L20,20"))
        assertEquals(listOf(
                Line(Vec2(10.0, 10.0), Vec2(20.0, 20.0))
        ), path.elements)
    }

    @Test
    fun `lineTo relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 l10,10"))
        assertEquals(listOf(
                Line(Vec2(10.0, 10.0), Vec2(20.0, 20.0))
        ), path.elements)
    }

    @Test
    fun `lineTo horizontal absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 H20"))
        assertEquals(listOf(
                Line(Vec2(10.0, 10.0), Vec2(20.0, 10.0))
        ), path.elements)
    }

    @Test
    fun `lineTo horizontal relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 h10"))
        assertEquals(listOf(
                Line(Vec2(10.0, 10.0), Vec2(20.0, 10.0))
        ), path.elements)
    }

    @Test
    fun `lineTo vertical absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 V20"))
        assertEquals(listOf(
                Line(Vec2(10.0, 10.0), Vec2(10.0, 20.0))
        ), path.elements)
    }

    @Test
    fun `lineTo vertical relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 v10"))
        assertEquals(listOf(
                Line(Vec2(10.0, 10.0), Vec2(10.0, 20.0))
        ), path.elements)
    }

    @Test
    fun `lineTo zero width`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 L10,10"))
        assertEquals(emptyList(), path.elements)
    }

    @Test
    fun `closePath triangle`() {
        val path = parser.parse(tokenizer.tokenize("M0,0 L10,0 L0,10 Z"))
        assertEquals(listOf(
                Line(Vec2(0.0, 0.0), Vec2(10.0, 0.0)),
                Line(Vec2(10.0, 0.0), Vec2(0.0, 10.0)),
                Line(Vec2(0.0, 10.0), Vec2(0.0, 0.0))
        ), path.elements)
    }

    @Test
    fun `closePath empty`() {
        val path = parser.parse(tokenizer.tokenize("M0,0 Z"))
        assertEquals(emptyList(), path.elements)
    }

    @Test
    fun `quadratic absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 Q20,20 30,10"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(30.0, 10.0), listOf(Vec2(20.0, 20.0)))
        ), path.elements)
    }

    @Test
    fun `quadratic relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 q10,10 20,0"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(30.0, 10.0), listOf(Vec2(20.0, 20.0)))
        ), path.elements)
    }

    @Test
    fun `quadratic shorthand absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 Q20,20 30,10 T50,10"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(30.0, 10.0), listOf(Vec2(20.0, 20.0))),
                Curve(Vec2(30.0, 10.0), Vec2(50.0, 10.0), listOf(Vec2(40.0, 0.0)))
        ), path.elements)
    }

    @Test
    fun `quadratic shorthand relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 Q20,20 30,10 t20,0"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(30.0, 10.0), listOf(Vec2(20.0, 20.0))),
                Curve(Vec2(30.0, 10.0), Vec2(50.0, 10.0), listOf(Vec2(40.0, 0.0)))
        ), path.elements)
    }

    @Test
    fun `cubic absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 C20,20 30,20 40,10"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(40.0, 10.0), listOf(Vec2(20.0, 20.0), Vec2(30.0, 20.0)))
        ), path.elements)
    }

    @Test
    fun `cubic relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 c10,10 20,10 30,0"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(40.0, 10.0), listOf(Vec2(20.0, 20.0), Vec2(30.0, 20.0)))
        ), path.elements)
    }

    @Test
    fun `cubic shorthand absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 C20,20 30,20 40,10 S60,0 70,10"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(40.0, 10.0), listOf(Vec2(20.0, 20.0), Vec2(30.0, 20.0))),
                Curve(Vec2(40.0, 10.0), Vec2(70.0, 10.0), listOf(Vec2(50.0, 0.0), Vec2(60.0, 0.0)))
        ), path.elements)
    }

    @Test
    fun `cubic shorthand relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 C20,20 30,20 40,10 s20,-10 30,0"))
        assertEquals(listOf(
                Curve(Vec2(10.0, 10.0), Vec2(40.0, 10.0), listOf(Vec2(20.0, 20.0), Vec2(30.0, 20.0))),
                Curve(Vec2(40.0, 10.0), Vec2(70.0, 10.0), listOf(Vec2(50.0, 0.0), Vec2(60.0, 0.0)))
        ), path.elements)
    }

    @Test
    fun `arc absolute`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 A20,10 90 0,1 30,10"))
        assertEquals(listOf(
                Arc(Vec2(10.0, 10.0), Vec2(30.0, 10.0), Vec2(20.0, 10.0),
                        Math.PI / 2, largeArc = false, sweep = true)
        ), path.elements)
    }

    @Test
    fun `arc relative`() {
        val path = parser.parse(tokenizer.tokenize("M10,10 a20,10 90 0,1 20,0"))
        assertEquals(listOf(
                Arc(Vec2(10.0, 10.0), Vec2(30.0, 10.0), Vec2(20.0, 10.0),
                        Math.PI / 2, largeArc = false, sweep = true)
        ), path.elements)
    }

    @Test(expected = SvgParseException::class)
    fun `arc bad boolean fail`() {
        parser.parse(tokenizer.tokenize("M10,10 a20,10 90 1223,1 20,0"))
    }

    @Test
    fun `arc bad boolean lenient`() {
        val path = parserLenient.parse(tokenizer.tokenize("M10,10 a20,10 90 1223,1 20,0"))
        assertEquals(listOf(
                Arc(Vec2(10.0, 10.0), Vec2(30.0, 10.0), Vec2(20.0, 10.0),
                        Math.PI / 2, largeArc = false, sweep = true)
        ), path.elements)
    }

}
