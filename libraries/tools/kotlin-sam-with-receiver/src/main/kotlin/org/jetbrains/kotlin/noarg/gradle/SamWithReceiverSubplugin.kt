/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.samWithReceiver.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.noarg.gradle.model.builder.SamWithReceiverModelBuilder
import javax.inject.Inject

class SamWithReceiverGradleSubplugin @Inject internal constructor(private val registry: ToolingModelBuilderRegistry) : Plugin<Project> {
    companion object {
        fun isEnabled(project: Project) = project.plugins.findPlugin(SamWithReceiverGradleSubplugin::class.java) != null
    }

    override fun apply(project: Project) {
        project.extensions.create("samWithReceiver", SamWithReceiverExtension::class.java)
        registry.register(SamWithReceiverModelBuilder())
    }
}

class SamWithReceiverKotlinGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
    companion object {
        const val SAM_WITH_RECEIVER_ARTIFACT_NAME = "kotlin-sam-with-receiver"

        private val ANNOTATION_ARG_NAME = "annotation"
        private val PRESET_ARG_NAME = "preset"
    }

    override fun isApplicable(project: Project, task: AbstractCompile) = SamWithReceiverGradleSubplugin.isEnabled(project)

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<*>?
    ): List<SubpluginOption> {
        if (!SamWithReceiverGradleSubplugin.isEnabled(project)) return emptyList()

        val samWithReceiverExtension = project.extensions.findByType(SamWithReceiverExtension::class.java) ?: return emptyList()

        val options = mutableListOf<SubpluginOption>()

        for (anno in samWithReceiverExtension.myAnnotations) {
            options += SubpluginOption(ANNOTATION_ARG_NAME, anno)
        }

        for (preset in samWithReceiverExtension.myPresets) {
            options += SubpluginOption(PRESET_ARG_NAME, preset)
        }

        return options
    }

    override fun getCompilerPluginId() = "org.jetbrains.kotlin.samWithReceiver"
    override fun getPluginArtifact(): SubpluginArtifact =
        JetBrainsSubpluginArtifact(artifactId = SAM_WITH_RECEIVER_ARTIFACT_NAME)
}