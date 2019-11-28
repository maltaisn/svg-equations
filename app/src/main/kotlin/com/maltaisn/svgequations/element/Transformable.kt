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

import com.maltaisn.svgequations.math.Mat33


interface Transformable<T> {

    /**
     * Transform this element using a transform matrix.
     * Returns a transformed of the element.
     * @param transform Transformation matrix.
     * @param scale Scale matrix only, to scale dimensions.
     */
    fun transform(transform: Mat33): T

}
