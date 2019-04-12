/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.allopen.ide

import org.jetbrains.kotlin.allopen.AllOpenCommandLineProcessor
import org.jetbrains.kotlin.annotation.plugin.ide.AbstractGradleImportHandler
import org.jetbrains.kotlin.utils.PathUtil

class AllOpenGradleProjectImportHandler : AbstractGradleImportHandler<AllOpenModel>() {
    override val compilerPluginId = AllOpenCommandLineProcessor.PLUGIN_ID
    override val pluginName = "allopen"
    override val annotationOptionName = AllOpenCommandLineProcessor.ANNOTATION_OPTION.optionName
    override val pluginJarFileFromIdea = PathUtil.kotlinPathsForIdeaPlugin.allOpenPluginJarPath
    override val modelKey = AllOpenProjectResolverExtension.KEY

    override fun getAnnotationsForPreset(presetName: String): List<String> {
        for ((name, annotations) in AllOpenCommandLineProcessor.SUPPORTED_PRESETS.entries) {
            if (presetName == name) {
                return annotations
            }
        }

        return super.getAnnotationsForPreset(presetName)
    }
}
