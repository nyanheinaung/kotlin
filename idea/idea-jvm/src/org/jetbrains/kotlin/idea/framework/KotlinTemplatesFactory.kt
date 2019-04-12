/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.framework

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.platform.ProjectTemplate
import com.intellij.platform.ProjectTemplatesFactory
import com.intellij.platform.templates.BuilderBasedTemplate
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.js.resolve.JsPlatform
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform

class KotlinTemplatesFactory : ProjectTemplatesFactory() {
    companion object {
        val EP_NAME = ExtensionPointName.create<ModuleBuilder>("org.jetbrains.kotlin.moduleBuilder")

        val KOTLIN_GROUP_NAME: String = "Kotlin"
    }

    override fun getGroups() = arrayOf(KOTLIN_GROUP_NAME)
    override fun getGroupIcon(group: String) = KotlinIcons.SMALL_LOGO

    override fun createTemplates(group: String?, context: WizardContext?): Array<out ProjectTemplate> {
        val result = mutableListOf<ProjectTemplate>(
                BuilderBasedTemplate(KotlinModuleBuilder(JvmPlatform,
                                                         "JVM | IDEA",
                                                         "Kotlin project with a JVM target based on the IntelliJ IDEA build system",
                                                         KotlinIcons.SMALL_LOGO)),

                BuilderBasedTemplate(KotlinModuleBuilder(JsPlatform, "JS | IDEA",
                                                         "Kotlin project with a JavaScript target based on the IntelliJ IDEA build system",
                                                         KotlinIcons.JS)
                )
        )
        result.addAll(Extensions.getExtensions(EP_NAME).map { BuilderBasedTemplate(it) })
        return result.toTypedArray()
    }
}
