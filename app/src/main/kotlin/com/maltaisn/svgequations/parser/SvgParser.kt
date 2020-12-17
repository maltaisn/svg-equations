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

import com.maltaisn.svgequations.Path
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Used to parse the `path` elements of a SVG file into [Path] classes.
 *
 * @param lenient If lenient, parser will ignore non-fatal errors.
 */
class SvgParser(val lenient: Boolean) {

    /**
     * Parse path data from a SVG [inputStream].
     * Can return an empty list if file contains no path data.
     * @throws SvgParseException Thrown on parse errors.
     */
    fun parse(inputStream: InputStream): List<PathElement> {
        // Parse SVG file (a XML file)
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val root = try {
            builder.parse(inputStream).documentElement
        } catch (e: SAXException) {
            parseError("XML parsing error: $e")
        } catch (e: IOException) {
            parseError("Could not read input file.")
        }
        // Find all <path> elements, regardless of how nested they are. This will almost always lead to misrendering
        // if path is not directly nested under the root <svg>, but still better than not showing them I guess.
        val pathElements = root.getElementsByTagName("path")

        // Collect path elements data
        val paths = mutableListOf<PathElement>()
        for (i in 0 until pathElements.length) {
            val pathElement = pathElements.item(i)
            val pathStr = pathElement.attributes.getNamedItem("d")?.nodeValue
            val transformStr = pathElement.attributes.getNamedItem("transform")?.nodeValue
            val colorStr = pathElement.attributes.getNamedItem("stroke")?.nodeValue
            val opacityStr = pathElement.attributes.getNamedItem("stroke-opacity")?.nodeValue
                ?: pathElement.attributes.getNamedItem("opacity")?.nodeValue
            if (pathStr != null && pathStr.isNotBlank() && pathStr != "none") {
                paths += PathElement(pathStr, transformStr, colorStr, opacityStr)
            }
        }

        return paths
    }

    fun parse(file: File): List<PathElement> {
        if (!file.exists()) {
            if (lenient) {
                return emptyList()
            } else {
                parseError("Input file doesn't exist.")
            }
        } else if (file.extension != "svg" && !lenient) {
            parseError("Input file is not a SVG file.")
        }
        return parse(file.inputStream())
    }

    data class PathElement(
        val path: String,
        val transform: String?,
        val color: String?,
        val opacity: String?,
    )

}
