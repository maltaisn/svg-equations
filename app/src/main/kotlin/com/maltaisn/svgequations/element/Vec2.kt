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

package com.maltaisn.svgequations.element


/**
 * A 2D point or dimension.
 */
data class Vec2(val x: Double = 0.0, val y: Double = 0.0) {

    operator fun plus(p: Vec2) = Vec2(x + p.x, y + p.y)

    operator fun minus(p: Vec2) = Vec2(x - p.x, y - p.y)

    operator fun times(s: Double) = Vec2(s * x, s * y)

    operator fun div(s: Double) = Vec2(x / s, y / s)

    operator fun unaryMinus() = Vec2(-x, -y)

    override fun toString() = "($x, $y)"

}
