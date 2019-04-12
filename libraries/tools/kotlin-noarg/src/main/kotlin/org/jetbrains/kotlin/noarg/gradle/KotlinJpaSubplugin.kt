/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinJpaSubplugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(NoArgGradleSubplugin::class.java)
        val noArgExtension = NoArgGradleSubplugin.getNoArgExtension(project)
        noArgExtension.myPresets += "jpa"
    }
}