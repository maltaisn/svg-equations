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

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


/**
 * A 3x3 matrix supporting some operations for 2D transformations.
 * ```
 * [m00 m01 m02]
 * [m10 m11 m12]
 * [m20 m21 m22]
 * ```
 */
data class Mat33(val m00: Double, val m01: Double, val m02: Double,
                 val m10: Double, val m11: Double, val m12: Double,
                 val m20: Double, val m21: Double, val m22: Double) {

    /** Matrix addition */
    operator fun plus(mat: Mat33) = Mat33(
            m00 + mat.m00, m01 + mat.m01, m02 + mat.m02,
            m10 + mat.m10, m11 + mat.m11, m12 + mat.m12,
            m20 + mat.m20, m21 + mat.m21, m22 + mat.m22)

    /** Matrix subtraction */
    operator fun minus(mat: Mat33) = Mat33(
            m00 - mat.m00, m01 - mat.m01, m02 - mat.m02,
            m10 - mat.m10, m11 - mat.m11, m12 - mat.m12,
            m20 - mat.m20, m21 - mat.m21, m22 - mat.m22)

    /** Multiplication by a scalar */
    operator fun times(k: Double) = Mat33(
            m00 * k, m01 * k, m02 * k,
            m10 * k, m11 * k, m12 * k,
            m20 * k, m21 * k, m22 * k)

    /** Multiplication by a 2D vector. The third coordinate is ignored. */
    operator fun times(vec: Vec2) = Vec2(
            m00 * vec.x + m01 * vec.y + m02,
            m10 * vec.x + m11 * vec.y + m12)

    /** Multiplication by a matrix */
    operator fun times(mat: Mat33) = Mat33(
            m00 * mat.m00 + m01 * mat.m10 + m02 * mat.m20,
            m00 * mat.m01 + m01 * mat.m11 + m02 * mat.m21,
            m00 * mat.m02 + m01 * mat.m12 + m02 * mat.m22,
            m10 * mat.m00 + m11 * mat.m10 + m12 * mat.m20,
            m10 * mat.m01 + m11 * mat.m11 + m12 * mat.m21,
            m10 * mat.m02 + m11 * mat.m12 + m12 * mat.m22,
            m20 * mat.m00 + m21 * mat.m10 + m22 * mat.m20,
            m20 * mat.m01 + m21 * mat.m11 + m22 * mat.m21,
            m20 * mat.m02 + m21 * mat.m12 + m22 * mat.m22)

    /** Negation, same as multiplication by -1. */
    operator fun unaryMinus() = Mat33(
            -m00, -m01, -m02,
            -m10, -m11, -m12,
            -m20, -m21, -m22)

    override fun toString() = "Mat33([$m00 $m01 $m02] [$m10 $m11 $m12] [$m20 $m21 $m22])"

    companion object {
        fun scale(sx: Double, sy: Double) = Mat33(
                sx, 0.0, 0.0,
                0.0, sy, 0.0,
                0.0, 0.0, 1.0)

        fun translate(dx: Double, dy: Double) = Mat33(
                1.0, 0.0, dx,
                0.0, 1.0, dy,
                0.0, 0.0, 1.0)

        fun rotation(angle: Double) = Mat33(
                cos(angle), -sin(angle), 0.0,
                sin(angle), cos(angle), 0.0,
                0.0, 0.0, 1.0)

        fun skew(x: Double, y: Double) = Mat33(
                1.0, tan(x), 0.0,
                tan(y), 1.0, 0.0,
                0.0, 0.0, 1.0)

        val IDENTITY = Mat33(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
    }

}
