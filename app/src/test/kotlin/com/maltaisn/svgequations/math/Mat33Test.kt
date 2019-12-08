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

package com.maltaisn.svgequations.math

import org.junit.Test
import kotlin.test.assertEquals


internal class Mat33Test {

    @Test
    fun `matrix multiplication vec2`() {
        val mat = Mat33(1.0, -2.0, 3.0, 7.0, 11.0, -8.0, 9.0, 5.0, -4.0)
        val vec = Vec2(13.0, 17.0)
        assertEquals(Vec2(-18.0, 270.0), mat * vec)
    }

    @Test
    fun `matrix multiplication`() {
        val mat1 = Mat33(0.0, 12.0, 11.0, 7.0, 5.0, -1.0, 3.0, 8.0, 6.0)
        val mat2 = Mat33(-7.0, -1.0, 2.0, 4.0, 13.0, 4.0, 6.0, 9.0, 11.0)
        assertEquals(Mat33(114.0, 255.0, 169.0, -35.0, 49.0, 23.0, 47.0, 155.0, 104.0), mat1 * mat2)
    }


    @Test
    fun `matrix multiplication with identity`() {
        val mat1 = Mat33(0.0, 12.0, 11.0, 7.0, 5.0, -1.0, 3.0, 8.0, 6.0)
        val mat2 = Mat33.IDENTITY
        assertEquals(mat1, mat1 * mat2)
    }

    @Test
    fun `matrix multiplication with identity 2`() {
        val mat1 = Mat33.IDENTITY
        val mat2 = Mat33(0.0, 12.0, 11.0, 7.0, 5.0, -1.0, 3.0, 8.0, 6.0)
        assertEquals(mat2, mat1 * mat2)
    }

}
