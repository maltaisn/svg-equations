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
 * An elliptic arc with a [start] point, an [end] point, a [radius] and a [rotation] (in radians).
 * See [https://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands] for more info.
 *
 * @param largeArc Large arc flag. If `true`, the largest of the two arcs is drawn.
 * @param sweep Sweep flag. If `true`, arc will be drawn in clockwise direction.
 */
data class Arc(override val start: Vec2, override val end: Vec2,
               val radius: Vec2, val rotation: Double,
               val largeArc: Boolean, val sweep: Boolean) : Element
