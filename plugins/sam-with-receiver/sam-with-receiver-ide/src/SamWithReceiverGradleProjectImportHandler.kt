/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.samWithReceiver.ide

import org.jetbrains.kotlin.annotation.plugin.ide.AbstractGradleImportHandler
import org.jetbrains.kotlin.samWithReceiver.SamWithReceiverCommandLineProcessor
import org.jetbrains.kotlin.utils.PathUtil

class SamWithReceiverGradleProjectImportHandler : AbstractGradleImportHandler<SamWithReceiverModel>() {
    override val compilerPluginId = SamWithReceiverCommandLineProcessor.PLUGIN_ID
    override val pluginName = "sam-with-receiver"
    override val annotationOptionName = SamWithReceiverCommandLineProcessor.ANNOTATION_OPTION.optionName
    override val pluginJarFileFromIdea = PathUtil.kotlinPathsForIdeaPlugin.samWithReceiverJarPath
    override val modelKey = SamWithReceiverProjectResolverExtension.KEY

    override fun getAnnotationsForPreset(presetName: String): List<String> {
        for ((name, annotations) in SamWithReceiverCommandLineProcessor.SUPPORTED_PRESETS.entries) {
            if (presetName == name) {
                return annotations
            }
        }

        return super.getAnnotationsForPreset(presetName)
    }
}
