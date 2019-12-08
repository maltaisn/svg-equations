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


/**
 * Used to split a SVG path string into commands and numeric values.
 * More info on SVG grammar at [https://www.w3.org/TR/SVG/paths.html#PathDataBNF].
 *
 * @param lenient If lenient, tokenizer will ignore non-fatal errors.
 */
class PathTokenizer(val lenient: Boolean) {

    private var valuesSinceCommand = 0

    private var tokens = PathTokens()


    /**
     * Tokenize a SVG [path string][pathStr].
     * @throws SvgParseException Thrown on parse errors.
     */
    fun tokenize(pathStr: String): PathTokens {
        valuesSinceCommand = 0

        tokens = PathTokens()

        // Parse commands and values
        var i = 0
        while (i < pathStr.length) {
            val c = pathStr[i]
            when {
                c == ',' || c.isWhitespace() -> {
                    i++
                }
                c.toUpperCase() in "MZLHVQTCSA" -> {
                    checkLastCommandArity(i)

                    // Add command
                    tokens.commands += c
                    valuesSinceCommand = 0
                    i++
                }
                else -> {
                    // Parse value starting at current index.
                    i = parseValue(pathStr, i)

                    // If there are too many values
                    val lastArity = lastCommandArity
                    if (valuesSinceCommand >= 2 * lastArity) {
                        if (lastArity == 0) {
                            // Path has a trailing value.
                            if (lenient) {
                                // Lenient, so just remove trailing value.
                                tokens.values.removeLast()
                                valuesSinceCommand--
                            } else {
                                parseError("Trailing values", i)
                            }
                        } else {
                            // Multiple values sets, repeat command.
                            val last = tokens.commands.last
                            tokens.commands += when (last) {
                                'M' -> 'L'
                                'm' -> 'l'
                                else -> last
                            }
                            valuesSinceCommand = lastArity
                        }
                    }
                }
            }
        }

        checkLastCommandArity(pathStr.length)

        return tokens
    }

    /**
     * Parse a list of values from a string, returns a list.
     */
    fun parseValues(valuesStr: String): List<Double> {
        tokens = PathTokens()
        var i = 0
        while (i < valuesStr.length) {
            val c = valuesStr[i]
            if (c != ',' && !c.isWhitespace()) {
                i = parseValue(valuesStr, i)
            } else {
                i++
            }
        }
        return tokens.values
    }

    /**
     * Parse value starting at a [start] position and add it to [tokens].
     * Throws parse error if value is invalid.
     */
    private fun parseValue(str: String, start: Int): Int {
        // Parse value starting at current index.
        val end = findValueEndIndex(str, start)
        val valueStr = str.substring(start, end)
        val value = valueStr.toDoubleOrNull()

        if (valueStr == "." || value == null) {
            // Invalid or empty number literal
            if (!lenient) {
                if (valueStr.isEmpty()) {
                    parseError("Invalid character '${str[start + 1]}'", start)
                } else {
                    parseError("Invalid number literal '$valueStr'", start)
                }
            } else if (valueStr.isEmpty()) {
                // Lenient and current char is invalid, so just skip it.
                return end + 1
            }
        } else {
            // Add value
            tokens.values += value
            valuesSinceCommand++
        }
        return end
    }

    /**
     * Find the end index of a value at a [start] position.
     */
    private fun findValueEndIndex(str: String, start: Int): Int {
        if (lastCommand == 'A' && valuesSinceCommand % 7 in 3..4 && str[start] in '0'..'1') {
            // Special case for valid syntax: "A10,10 0 1110,10" equivalent to "A10,10 0 1 1 10,10".
            return start + 1
        }

        var i = start
        var valueHasPoint = false
        var exponentPos = -1
        while (i < str.length) {
            when (str[i]) {
                '.' -> {
                    if (valueHasPoint) {
                        return i
                    } else {
                        valueHasPoint = true
                    }
                }
                'e', 'E' -> {
                    if (exponentPos != -1) {
                        return i
                    } else {
                        exponentPos = i
                    }
                }
                '+', '-' -> {
                    if (i != start && i != exponentPos + 1) {
                        return i
                    }
                }
                !in '0'..'9' -> {
                    return i
                }
            }
            i++
        }
        return i
    }

    /**
     * Check if last command has the correct number of values.
     */
    private fun checkLastCommandArity(i: Int) {
        // Check if previous command has the right number of values.
        val lastArity = lastCommandArity
        if (valuesSinceCommand < lastArity) {
            // Not enough values. Even lenient can't recover from this.
            parseError("Not enough values on command", i)
        } else if (valuesSinceCommand > lastArity) {
            // Too many values.
            if (lenient) {
                // Lenient, so remove extra values.
                while (valuesSinceCommand > lastArity) {
                    tokens.values.removeLast()
                    valuesSinceCommand--
                }
            } else {
                parseError("Too many values on command", i)
            }
        }
    }

    private val lastCommand: Char?
        get() = tokens.commands.lastOrNull()?.toUpperCase()

    private val lastCommandArity: Int
        get() = COMMAND_ARITY[lastCommand] ?: 0

    companion object {
        /** The number of values each command expects to find. */
        private val COMMAND_ARITY = mapOf(
                'M' to 2,
                'Z' to 0,
                'L' to 2,
                'H' to 1,
                'V' to 1,
                'Q' to 4,
                'T' to 2,
                'C' to 6,
                'S' to 4,
                'A' to 7)
    }
}
