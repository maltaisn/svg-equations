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

import org.junit.Test
import java.text.DecimalFormat
import kotlin.test.assertEquals


internal class EquationFormatterTest {

    private val formatter = EquationFormatter(DecimalFormat().apply {
        maximumFractionDigits = 2
    })

    @Test
    fun `single non zero term`() {
        val equation = formatter.format(listOf(2.0 to "x"))
        assertEquals("2x", equation)
    }

    @Test
    fun `single non zero term with many decimals`() {
        val equation = formatter.format(listOf(3.14159265 to "x"))
        assertEquals("3.14x", equation)
    }

    @Test
    fun `single zero term`() {
        val equation = formatter.format(listOf(0.0 to "x"))
        assertEquals("0", equation)
    }

    @Test
    fun `single zero term when rounded`() {
        val equation = formatter.format(listOf(0.001 to "x"))
        assertEquals("0", equation)
    }

    @Test
    fun `single negative term`() {
        val equation = formatter.format(listOf(-2.0 to "x"))
        assertEquals("-2x", equation)
    }

    @Test
    fun `single negative term, zero when rounded`() {
        val equation = formatter.format(listOf(-0.001 to "x"))
        assertEquals("0", equation)
    }

    @Test
    fun `single empty term`() {
        val equation = formatter.format(listOf(2.0 to ""))
        assertEquals("2", equation)
    }

    @Test
    fun `multiple non zero terms`() {
        val equation = formatter.format(listOf(2.0 to "x^2", 3.0 to "x", 4.0 to ""))
        assertEquals("2x^2 + 3x + 4", equation)
    }

    @Test
    fun `multiple terms with zero and negative`() {
        val equation = formatter.format(listOf(2.0 to "x^2", 0.0 to "x", -4.0 to ""))
        assertEquals("2x^2 - 4", equation)
    }

    @Test
    fun `multiple one terms`() {
        val equation = formatter.format(listOf(1.0 to "a", -1.0 to "b", 1.001 to "c", -0.999 to "d"))
        assertEquals("a - b + c - d", equation)
    }

    @Test
    fun `bounds greater than`() {
        val equation = formatter.format(listOf(2.0 to "a"), boundsSymbol = 'a', boundsStart = 0.0)
        assertEquals("2a {a >= 0}", equation)
    }

    @Test
    fun `bounds less than`() {
        val equation = formatter.format(listOf(2.0 to "a"), boundsSymbol = 'a', boundsEnd = 0.0)
        assertEquals("2a {a <= 0}", equation)
    }

    @Test
    fun `bounds interval`() {
        val equation = formatter.format(listOf(2.0 to "a"), boundsSymbol = 'a', boundsStart = -10.0, boundsEnd = 10.0)
        assertEquals("2a {-10 <= a <= 10}", equation)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `bounds interval wrong`() {
        formatter.format(listOf(2.0 to "a"), boundsSymbol = 'a', boundsStart = 10.0, boundsEnd = -10.0)
    }

}
