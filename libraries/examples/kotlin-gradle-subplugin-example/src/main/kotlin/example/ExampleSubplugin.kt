/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package example

import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.plugin.*

class ExampleSubplugin : KotlinGradleSubplugin<AbstractCompile> {

    override fun isApplicable(project: Project, task: AbstractCompile): Boolean {
        return true
    }

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<*>?
    ): List<SubpluginOption> {
        println("ExampleSubplugin loaded")
        return listOf(SubpluginOption("exampleKey", "exampleValue"))
    }

    override fun getCompilerPluginId(): String {
        return "example.plugin"
    }

    override fun getPluginArtifact(): SubpluginArtifact =
        JetBrainsSubpluginArtifact("kotlin-gradle-subplugin-example")
}