/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg.ide

import org.jetbrains.kotlin.annotation.plugin.ide.AbstractGradleImportHandler
import org.jetbrains.kotlin.annotation.plugin.ide.AnnotationBasedCompilerPluginSetup.PluginOption
import org.jetbrains.kotlin.noarg.NoArgCommandLineProcessor
import org.jetbrains.kotlin.utils.PathUtil

class NoArgGradleProjectImportHandler : AbstractGradleImportHandler<NoArgModel>() {
    override val compilerPluginId = NoArgCommandLineProcessor.PLUGIN_ID
    override val pluginName = "noarg"
    override val annotationOptionName = NoArgCommandLineProcessor.ANNOTATION_OPTION.optionName
    override val pluginJarFileFromIdea = PathUtil.kotlinPathsForIdeaPlugin.noArgPluginJarPath
    override val modelKey = NoArgProjectResolverExtension.KEY

    override fun getAdditionalOptions(model: NoArgModel): List<PluginOption> {
        return listOf(PluginOption(
                NoArgCommandLineProcessor.INVOKE_INITIALIZERS_OPTION.optionName,
                model.invokeInitializers.toString()))
    }

    override fun getAnnotationsForPreset(presetName: String): List<String> {
        for ((name, annotations) in NoArgCommandLineProcessor.SUPPORTED_PRESETS.entries) {
            if (presetName == name) {
                return annotations
            }
        }

        return super.getAnnotationsForPreset(presetName)
    }
}
