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

import java.util.*


/**
 * Used to split a SVG path string into commands and numeric values.
 * More info on SVG grammar at [https://www.w3.org/TR/SVG/paths.html#PathDataBNF].
 *
 * @param lenient If lenient, tokenizer will ignore non-fatal errors.
 */
class PathTokenizer(val lenient: Boolean) {

    private lateinit var pathStr: String

    private var tokens = Tokens()
    private var valueStart = -1
    private var valueHasPoint = false
    private var valueHasSign = false
    private var valuesSinceCommand = 0


    /**
     * Tokenize a SVG [path string][pathStr].
     * @throws SvgParseException Thrown on parse errors.
     */
    fun tokenize(pathStr: String): Tokens {
        this.pathStr = pathStr

        tokens = Tokens()
        valueStart = -1
        valueHasPoint = false
        valueHasSign = false
        valuesSinceCommand = 0

        // Parse commands and values
        for (i in 0..pathStr.length) {
            val char = pathStr.getOrNull(i)
            when {
                char == null || char.isWhitespace() || char == ',' -> {
                    // Value has ended.
                    pushValue(i)
                }
                char == '.' -> {
                    if (valueHasPoint || valueStart == -1) {
                        // Start pos of a new value.
                        pushValue(i)
                        valueStart = i
                    } else {
                        // Decimal point of current value.
                        valueHasPoint = true
                    }
                }
                char == '+' || char == '-' -> {
                    // Start pos of a value.
                    if (valueStart == -1 || valueHasSign) {
                        pushValue(i)
                        valueStart = i
                    }
                    valueHasSign = true
                }
                char in '0'..'9' -> {
                    // Start pos of a value.
                    if (valueStart == -1) {
                        pushValue(i)
                        valueStart = i
                    }
                }
                "MZLHVQTCSA".contains(char, ignoreCase = true) -> {
                    // Command
                    pushValue(i)

                    val requiredValues = getCommandArity(tokens.commands.lastOrNull())
                    if (requiredValues != valuesSinceCommand) {
                        if (lenient && valuesSinceCommand > requiredValues) {
                            // Lenient and there's too many values. Ignore them
                            while (valuesSinceCommand > requiredValues) {
                                tokens.values.removeLast()
                                valuesSinceCommand--
                            }
                        } else {
                            parseError("Path has incorrect number of values for command", i)
                        }
                    }
                    tokens.commands += char
                    valuesSinceCommand = 0
                }
                else -> {
                    if (!lenient) {
                        // If lenient, ignore the unknown character.
                        parseError("Unknown character '$char'", i)
                    }
                }
            }

            val lastCommand = tokens.commands.lastOrNull()
            val commandArity = getCommandArity(lastCommand)
            if (valuesSinceCommand > commandArity) {
                if (commandArity == 0) {
                    // Values before first command or after 'Z'.
                    if (lenient) {
                        // If lenient, just remove trailing value.
                        tokens.values.removeLast()
                        valuesSinceCommand--
                    } else {
                        parseError("Path has trailing values", i)
                    }
                } else {
                    // Multiple sets of coordinates, repeat command.
                    tokens.commands += when (lastCommand) {
                        'M' -> 'L'
                        'm' -> 'l'
                        else -> lastCommand!!
                    }
                    valuesSinceCommand = 1
                }
            }
        }

        return tokens
    }

    private fun pushValue(end: Int) {
        if (valueStart == -1) return
        var valueStr = pathStr.substring(valueStart, end)
        if (lenient) {
            // Remove unknown characters that were ignored
            valueStr = valueStr.replace(NUMBER_CHARS_REGEX, "")
        }

        val value = valueStr.toDoubleOrNull()
        if (value == null) {
            if (!lenient) {
                // If lenient, skip the invalid value.
                parseError("Invalid number literal '$valueStr' in path", valueStart)
            }
        } else {
            tokens.values += value
            valuesSinceCommand++
        }
        valueStart = -1
        valueHasPoint = false
        valueHasSign = false
    }

    private fun getCommandArity(command: Char?) = COMMAND_ARITY[command?.toUpperCase()] ?: 0

    class Tokens {
        val commands = LinkedList<Char>()
        val values = LinkedList<Double>()
    }

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

        private val NUMBER_CHARS_REGEX = """[^+\-.0-9]""".toRegex()
    }
}
