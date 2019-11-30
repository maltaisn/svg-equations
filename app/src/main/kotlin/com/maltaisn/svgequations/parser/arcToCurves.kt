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
import kotlin.math.*


/**
 * Approximate a SVG arc as cubic bezier curves, which are more easily transformable.
 * This is based on: [https://github.com/fontello/svgpath/blob/master/lib/a2c.js].
 * See [https://www.w3.org/TR/SVG/paths.html#PathDataEllipticalArcCommands] for more info on SVG arcs.
 */
fun arcToCurves(p1: Vec2, p2: Vec2, r: Vec2, phi: Double,
                largeArc: Boolean, sweep: Boolean): List<Curve> {
    if (p1 == p2) {
        // Start and end points are the same so arc is invisible.
        return emptyList()
    } else if (r.x == 0.0 || r.y == 0.0) {
        // Either radius is zero, treat arc as line.
        return listOf(listOf(p1, p2))
    }

    val cosphi = cos(phi)
    val sinphi = sin(phi)

    // Step 1: Move ellipse so origin is middle point between start and end points.
    // Also rotate it to line up ellipse axes with the XY axes.
    val x1p = cosphi * (p1.x - p2.x) / 2 + sinphi * (p1.y - p2.y) / 2
    val y1p = -sinphi * (p1.x - p2.x) / 2 + cosphi * (p1.y - p2.y) / 2

    var rx = r.x.absoluteValue
    var ry = r.y.absoluteValue
    val rxSq = rx.pow(2)
    val rySq = ry.pow(2)
    val x1pSq = x1p.pow(2)
    val y1pSq = y1p.pow(2)

    // Compensate out-of-range radii
    val lambda = sqrt(x1pSq / rxSq + y1pSq / rySq)
    if (lambda > 1.0) {
        rx *= lambda
        ry *= lambda
    }

    // Get arc center coordinates and angles. Step 1 is was done previously.
    // More info at: https://www.w3.org/TR/SVG11/implnote.html#ArcImplementationNotes

    // Step 2: Compute coordinates of the center in this new coordinate system.
    val t = rxSq * y1pSq + rySq * x1pSq
    var radicant = ((rxSq * rySq - t) / t).coerceAtLeast(0.0)
    radicant = sqrt(radicant) * if (largeArc == sweep) -1 else 1

    val cxp = radicant * rx / ry * y1p
    val cyp = radicant * -ry / rx * x1p

    // Step 3: Transform back to get coordinates of center in original coordinate system.
    val cx = cosphi * cxp - sinphi * cyp + (p1.x + p2.x) / 2
    val cy = sinphi * cxp + cosphi * cyp + (p1.y + p2.y) / 2

    // Step 4: compute start angle and extent angle.
    val v1 = Vec2((x1p - cxp) / rx, (y1p - cyp) / ry)
    val v2 = Vec2(-(x1p + cxp) / rx, -(y1p + cyp) / ry)

    val startAngle = unitVectorAngle(Vec2(1.0, 0.0), v1)

    var extent = unitVectorAngle(v1, v2)
    if (!sweep && extent > 0) {
        extent -= TAU
    }
    if (sweep && extent < 0) {
        extent += TAU
    }

    // Split arc into multiple segments, each less than 90 degrees.
    val segmentCount = ceil(extent.absoluteValue / (TAU / 4)).toInt().coerceAtLeast(1)
    val segmentLength = extent / segmentCount
    val curves = List(segmentCount) {
        approximateUnitArc(startAngle + it * segmentLength, segmentLength)
    }

    // Convert bezier approximation of unit circle to original ellipse
    return curves.map { curve ->
        curve.map { p ->
            val x = p.x * rx
            val y = p.y * ry
            val xp = cosphi * x - sinphi * y
            val yp = sinphi * x + cosphi * y
            Vec2(xp + cx, yp + cy)
        }
    }
}

/**
 * Approximate an arc of the unit circle with a cubic bezier curve.
 * See: [http://math.stackexchange.com/questions/873224].
 */
private fun approximateUnitArc(startAngle: Double, extent: Double): Curve {
    val alpha = 4.0 / 3.0 * tan(extent / 4)
    val start = Vec2(cos(startAngle), sin(startAngle))
    val end = Vec2(cos(startAngle + extent), sin(startAngle + extent))
    val c1 = Vec2(start.x - start.y * alpha, start.y + start.x * alpha)
    val c2 = Vec2(end.x + end.y * alpha, end.y - end.x * alpha)
    return listOf(start, c1, c2, end)
}

/**
 * Returns the angle between two unit vectors, [v1] and [v2].
 */
private fun unitVectorAngle(v1: Vec2, v2: Vec2): Double {
    val sign = if (v1 cross v2 < 0) -1 else 1
    val dot = (v1 dot v2).coerceIn(-1.0, 1.0)
    return sign * acos(dot)
}

private const val TAU = Math.PI * 2
