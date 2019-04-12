/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compiler.plugin

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

interface CommandLineProcessor {
    val pluginId: String
    val pluginOptions: Collection<AbstractCliOption>

    @Throws(CliOptionProcessingException::class)
    fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        @Suppress("DEPRECATION")
        processOption(option as CliOption, value, configuration)
    }

    // TODO remove processOption(AbstractCliOption, ...) implementation after removal of this.
    @Deprecated("Implement processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) instead.")
    @Throws(CliOptionProcessingException::class)
    fun processOption(option: CliOption, value: String, configuration: CompilerConfiguration) {}

    fun <T> CompilerConfiguration.appendList(option: CompilerConfigurationKey<List<T>>, value: T) {
        val paths = getList(option).asMutableList()
        paths.add(value)
        put(option, paths)
    }

    fun <T> CompilerConfiguration.appendList(option: CompilerConfigurationKey<List<T>>, values: List<T>) {
        val paths = getList(option).asMutableList()
        paths.addAll(values)
        put(option, paths)
    }

    fun CompilerConfiguration.applyOptionsFrom(map: Map<String, List<String>>, pluginOptions: Collection<AbstractCliOption>) {
        for ((key, values) in map) {
            val option = pluginOptions.firstOrNull { it.optionName == key } ?: continue

            for (value in values) {
                processOption(option, value, this)
            }
        }
    }

    private fun <T> List<T>.asMutableList(): MutableList<T> {
        if (this is ArrayList<T>) {
            return this
        }

        return this.toMutableList()
    }
}