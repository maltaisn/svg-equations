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

package com.maltaisn.svgequations

/**
 * Inline color class, stored in ARGB format.
 */
inline class Color(val value: Int) {

    /**
     * Create a color from [red][r], [green][g], [blue][b] and optionally [alpha][a] component values.
     * No range check or masking is done, values are expected to be between 0 and 255.
     */
    constructor(r: Int, g: Int, b: Int, a: Int = COMPONENT_MAX) :
            this((a shl SHIFT_ALPHA) or (r shl SHIFT_RED) or
                    (g shl SHIFT_GREEN) or (b shl SHIFT_BLUE))

    constructor(value: Int, alpha: Boolean = false) :
            this(if (alpha) value else value and MASK_RGB)

    val a: Int get() = value ushr SHIFT_ALPHA and MASK_BYTE
    val r: Int get() = value ushr SHIFT_RED and MASK_BYTE
    val g: Int get() = value ushr SHIFT_GREEN and MASK_BYTE
    val b: Int get() = value ushr SHIFT_BLUE and MASK_BYTE

    fun withAlpha(a: Int) = Color(value and MASK_RGB or (a shl SHIFT_ALPHA))

    override fun toString() = '#' + value.toString(16)

    companion object {
        val BLACK = Color(0, 0, 0)

        const val COMPONENT_MAX = 255

        const val SHIFT_ALPHA = 24
        const val SHIFT_RED = 16
        const val SHIFT_GREEN = 8
        const val SHIFT_BLUE = 0

        const val MASK_RGB = 0xffffff
        const val MASK_BYTE = 0xff

        /** Parse color like #rgb, #argb, #rrggbb, #aarrggbb. */
        fun hex(hex: String): Color {
            val value = hex.substring(1).toIntOrNull(16)
                ?: throw IllegalArgumentException("Bad hex color")
            return when (hex.length) {
                4, 5 -> {
                    val a = if (hex.length == 4) 0xf else value ushr 12 and 0xf
                    val r = value ushr 8 and 0xf
                    val g = value ushr 4 and 0xf
                    val b = value and 0xf
                    Color(r or (r shl 4), g or (g shl 4), b or (b shl 4), a or (a shl 4))
                }
                7, 9 -> Color(value, hex.length == 9)
                else -> throw IllegalArgumentException("Bad hex color")
            }
        }
    }
}