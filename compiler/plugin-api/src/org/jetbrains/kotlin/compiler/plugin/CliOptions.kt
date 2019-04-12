/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compiler.plugin

import java.util.regex.Pattern

interface AbstractCliOption {
    val optionName: String
    val valueDescription: String
    val description: String
    val required: Boolean
    val allowMultipleOccurrences: Boolean

    val deprecatedName: String?
        get() = null
}

class CliOption(
    override val optionName: String,
    override val valueDescription: String,
    override val description: String,
    override val required: Boolean = true,
    override val allowMultipleOccurrences: Boolean = false
) : AbstractCliOption {
    @Deprecated("Use optionName instead.", ReplaceWith("optionName"))
    val name: String
        get() = optionName
}

open class CliOptionProcessingException(message: String, cause: Throwable? = null): RuntimeException(message, cause)

class PluginCliOptionProcessingException(
        val pluginId: String,
        val options: Collection<AbstractCliOption>,
        message: String,
        cause: Throwable? = null
): CliOptionProcessingException(message, cause)

fun cliPluginUsageString(pluginId: String, options: Collection<AbstractCliOption>): String {
    val LEFT_INDENT = 2
    val MAX_OPTION_WIDTH = 26

    val renderedOptions = options.map {
        val name = "${it.optionName} ${it.valueDescription}"
        val margin = if (name.length > MAX_OPTION_WIDTH) {
            "\n" + " ".repeat(MAX_OPTION_WIDTH + LEFT_INDENT + 1)
        } else " ".repeat(1 + MAX_OPTION_WIDTH - name.length)

        val modifiers = listOfNotNull(
                if (it.required) "required" else null,
                if (it.allowMultipleOccurrences) "multiple" else null)
        val modifiersEnclosed = if (modifiers.isEmpty()) "" else " (${modifiers.joinToString()})"

        " ".repeat(LEFT_INDENT) + name + margin + it.description + modifiersEnclosed
    }
    return "Plugin \"$pluginId\" usage:\n" + renderedOptions.joinToString("\n", postfix = "\n")
}

data class CliOptionValue(
        val pluginId: String,
        val optionName: String,
        val value: String
) {
    override fun toString() = "$pluginId:$optionName=$value"
}

fun parsePluginOption(argumentValue: String): CliOptionValue? {
    val pattern = Pattern.compile("""^plugin:([^:]*):([^=]*)=(.*)$""")
    val matcher = pattern.matcher(argumentValue)
    if (matcher.matches()) {
        return CliOptionValue(matcher.group(1), matcher.group(2), matcher.group(3))
    }

    return null
}

fun getPluginOptionString(pluginId: String, key: String, value: String): String {
    return "plugin:$pluginId:$key=$value"
}