/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.samWithReceiver.ide

import org.jetbrains.kotlin.annotation.plugin.ide.AbstractMavenImportHandler
import org.jetbrains.kotlin.annotation.plugin.ide.AnnotationBasedCompilerPluginSetup.PluginOption
import org.jetbrains.kotlin.samWithReceiver.SamWithReceiverCommandLineProcessor
import org.jetbrains.kotlin.utils.PathUtil

class SamWithReceiverMavenProjectImportHandler : AbstractMavenImportHandler() {
    private companion object {
        val ANNOTATION_PARAMETER_PREFIX = "sam-with-receiver:${SamWithReceiverCommandLineProcessor.ANNOTATION_OPTION.optionName}="
    }

    override val compilerPluginId = SamWithReceiverCommandLineProcessor.PLUGIN_ID
    override val pluginName = "samWithReceiver"
    override val mavenPluginArtifactName = "kotlin-maven-sam-with-receiver"
    override val pluginJarFileFromIdea = PathUtil.kotlinPathsForIdeaPlugin.allOpenPluginJarPath

    override fun getOptions(enabledCompilerPlugins: List<String>, compilerPluginOptions: List<String>): List<PluginOption>? {
        if ("sam-with-receiver" !in enabledCompilerPlugins) {
            return null
        }

        val annotations = mutableListOf<String>()

        for ((presetName, presetAnnotations) in SamWithReceiverCommandLineProcessor.SUPPORTED_PRESETS) {
            if (presetName in enabledCompilerPlugins) {
                annotations.addAll(presetAnnotations)
            }
        }

        annotations.addAll(compilerPluginOptions.mapNotNull { text ->
            if (!text.startsWith(ANNOTATION_PARAMETER_PREFIX)) return@mapNotNull null
            text.substring(ANNOTATION_PARAMETER_PREFIX.length)
        })

        return annotations.map { PluginOption(SamWithReceiverCommandLineProcessor.ANNOTATION_OPTION.optionName, it) }
    }
}
