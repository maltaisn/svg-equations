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

import com.maltaisn.svgequations.element.Path
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
    fun parse(inputStream: InputStream): List<String> {
        // Parse SVG file (a XML file)
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val root = try {
            builder.parse(inputStream).documentElement
        } catch (e: SAXException) {
            parseError("XML parsing error: $e")
        } catch (e: IOException) {
            parseError("Could not read input file.")
        }
        val pathElements = root.getElementsByTagName("path")

        // Collect path elements data
        val paths = mutableListOf<String>()
        for (i in 0 until pathElements.length) {
            val pathElement = pathElements.item(i)
            val pathAttr = pathElement.attributes.getNamedItem("d")
            val pathStr = pathAttr?.nodeValue
            if (pathStr != null && pathStr.isNotBlank() && pathStr != "none") {
                paths += pathStr
            }
        }

        return paths
    }

    fun parse(file: File): List<String> {
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

}