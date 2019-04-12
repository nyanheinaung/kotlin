/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class SerializationGradleSubplugin : Plugin<Project> {
    companion object {
        fun isEnabled(project: Project) = project.plugins.findPlugin(SerializationGradleSubplugin::class.java) != null
    }

    override fun apply(project: Project) {
        // nothing here
    }
}

class SerializationKotlinGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
    companion object {
        const val SERIALIZATION_GROUP_NAME = "org.jetbrains.kotlin"
        const val SERIALIZATION_ARTIFACT_NAME = "kotlin-serialization"
        const val SERIALIZATION_ARTIFACT_UNSHADED_NAME = "kotlin-serialization-unshaded"
    }

    override fun isApplicable(project: Project, task: AbstractCompile): Boolean =
        SerializationGradleSubplugin.isEnabled(project)

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<*>?
    ): List<SubpluginOption> {
        return emptyList()
    }

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(SERIALIZATION_GROUP_NAME, SERIALIZATION_ARTIFACT_NAME)

    override fun getNativeCompilerPluginArtifact(): SubpluginArtifact? =
        SubpluginArtifact(SERIALIZATION_GROUP_NAME, SERIALIZATION_ARTIFACT_UNSHADED_NAME)

    override fun getCompilerPluginId() = "org.jetbrains.kotlinx.serialization"
}
