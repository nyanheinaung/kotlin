/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.allopen.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.jetbrains.kotlin.allopen.gradle.model.builder.AllOpenModelBuilder
import org.jetbrains.kotlin.gradle.plugin.*
import javax.inject.Inject

class AllOpenGradleSubplugin @Inject internal constructor(private val registry: ToolingModelBuilderRegistry) : Plugin<Project> {
    companion object {
        fun isEnabled(project: Project) = project.plugins.findPlugin(AllOpenGradleSubplugin::class.java) != null

        fun getAllOpenExtension(project: Project): AllOpenExtension {
            return project.extensions.getByType(AllOpenExtension::class.java)
        }
    }

    override fun apply(project: Project) {
        project.extensions.create("allOpen", AllOpenExtension::class.java)
        registry.register(AllOpenModelBuilder())
    }
}

class AllOpenKotlinGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
    companion object {
        const val ALLOPEN_ARTIFACT_NAME = "kotlin-allopen"

        private val ANNOTATION_ARG_NAME = "annotation"
        private val PRESET_ARG_NAME = "preset"
    }

    override fun isApplicable(project: Project, task: AbstractCompile) = AllOpenGradleSubplugin.isEnabled(project)

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<*>?
    ): List<SubpluginOption> {
        if (!AllOpenGradleSubplugin.isEnabled(project)) return emptyList()

        val allOpenExtension = project.extensions.findByType(AllOpenExtension::class.java) ?: return emptyList()

        val options = mutableListOf<SubpluginOption>()

        for (anno in allOpenExtension.myAnnotations) {
            options += SubpluginOption(ANNOTATION_ARG_NAME, anno)
        }

        for (preset in allOpenExtension.myPresets) {
            options += SubpluginOption(PRESET_ARG_NAME, preset)
        }

        return options
    }

    override fun getCompilerPluginId() = "org.jetbrains.kotlin.allopen"
    override fun getPluginArtifact(): SubpluginArtifact =
        JetBrainsSubpluginArtifact(artifactId = ALLOPEN_ARTIFACT_NAME)
}