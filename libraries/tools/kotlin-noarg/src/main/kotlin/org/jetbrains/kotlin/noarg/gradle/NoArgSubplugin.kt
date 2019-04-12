/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.noarg.gradle.model.builder.NoArgModelBuilder
import javax.inject.Inject

class NoArgGradleSubplugin @Inject internal constructor(private val registry: ToolingModelBuilderRegistry) : Plugin<Project> {
    companion object {
        fun isEnabled(project: Project) = project.plugins.findPlugin(NoArgGradleSubplugin::class.java) != null

        fun getNoArgExtension(project: Project): NoArgExtension {
            return project.extensions.getByType(NoArgExtension::class.java)
        }
    }

    override fun apply(project: Project) {
        project.extensions.create("noArg", NoArgExtension::class.java)
        registry.register(NoArgModelBuilder())
    }
}

class NoArgKotlinGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
    companion object {
        const val NOARG_ARTIFACT_NAME = "kotlin-noarg"

        private val ANNOTATION_ARG_NAME = "annotation"
        private val PRESET_ARG_NAME = "preset"
        private val INVOKE_INITIALIZERS_ARG_NAME = "invokeInitializers"
    }

    override fun isApplicable(project: Project, task: AbstractCompile) = NoArgGradleSubplugin.isEnabled(project)

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<*>?
    ): List<SubpluginOption> {
        if (!NoArgGradleSubplugin.isEnabled(project)) return emptyList()

        val noArgExtension = project.extensions.findByType(NoArgExtension::class.java) ?: return emptyList()

        val options = mutableListOf<SubpluginOption>()

        for (anno in noArgExtension.myAnnotations) {
            options += SubpluginOption(ANNOTATION_ARG_NAME, anno)
        }

        for (preset in noArgExtension.myPresets) {
            options += SubpluginOption(PRESET_ARG_NAME, preset)
        }

        if (noArgExtension.invokeInitializers) {
            options += SubpluginOption(INVOKE_INITIALIZERS_ARG_NAME, "true")
        }

        return options
    }

    override fun getCompilerPluginId() = "org.jetbrains.kotlin.noarg"
    override fun getPluginArtifact(): SubpluginArtifact =
        JetBrainsSubpluginArtifact(artifactId = NOARG_ARTIFACT_NAME)
}