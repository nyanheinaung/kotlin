/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.internal

import org.jetbrains.kotlin.gradle.plugin.CompositeSubpluginOption
import org.jetbrains.kotlin.gradle.plugin.FilesSubpluginOption
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.tasks.CompilerPluginOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.util.*

fun encodePluginOptions(options: Map<String, List<String>>): String {
    val os = ByteArrayOutputStream()
    val oos = ObjectOutputStream(os)

    oos.writeInt(options.size)
    for ((key, values) in options.entries) {
        oos.writeUTF(key)

        oos.writeInt(values.size)
        for (value in values) {
            oos.writeUTF(value)
        }
    }

    oos.flush()
    return Base64.getEncoder().encodeToString(os.toByteArray())
}

internal fun CompilerPluginOptions.withWrappedKaptOptions(
    withApClasspath: Iterable<File>,
    changedFiles: List<File> = emptyList(),
    classpathChanges: List<String> = emptyList(),
    compiledSourcesDir: List<File> = emptyList(),
    processIncrementally: Boolean = false
): CompilerPluginOptions {
    val resultOptionsByPluginId: MutableMap<String, List<SubpluginOption>> =
        subpluginOptionsByPluginId.toMutableMap()

    resultOptionsByPluginId.compute(Kapt3KotlinGradleSubplugin.KAPT_SUBPLUGIN_ID) { _, kaptOptions ->
        val changedFilesOption = FilesSubpluginOption("changedFile", changedFiles).takeIf { changedFiles.isNotEmpty() }
        val classpathChangesOption = SubpluginOption(
            "classpathChange",
            classpathChanges.joinToString(separator = File.pathSeparator)
        ).takeIf { classpathChanges.isNotEmpty() }
        val processIncrementallyOption = SubpluginOption("processIncrementally", processIncrementally.toString())
        val compiledSourcesOption =
            FilesSubpluginOption("compiledSourcesDir", compiledSourcesDir).takeIf { compiledSourcesDir.isNotEmpty() }

        val kaptOptionsWithClasspath =
            kaptOptions.orEmpty() +
                    withApClasspath.map { FilesSubpluginOption("apclasspath", listOf(it)) } +
                    changedFilesOption +
                    classpathChangesOption +
                    compiledSourcesOption +
                    processIncrementallyOption

        wrapPluginOptions(kaptOptionsWithClasspath.filterNotNull(), "configuration")
    }

    val result = CompilerPluginOptions()
    resultOptionsByPluginId.forEach { pluginId, options ->
        options.forEach { option -> result.addPluginArgument(pluginId, option) }
    }
    return result
}

fun wrapPluginOptions(options: List<SubpluginOption>, newOptionName: String): List<SubpluginOption> {
    val encodedOptions = lazy {
        val groupedOptions = options
            .groupBy { it.key }
            .mapValues { (_, options) -> options.map { it.value } }
        encodePluginOptions(groupedOptions)
    }
    val singleOption = CompositeSubpluginOption(newOptionName, encodedOptions, options)
    return listOf(singleOption)
}