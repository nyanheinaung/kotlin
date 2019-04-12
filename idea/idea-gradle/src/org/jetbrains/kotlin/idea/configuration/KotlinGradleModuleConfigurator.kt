/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import com.intellij.facet.FacetManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.core.isAndroidModule
import org.jetbrains.kotlin.idea.versions.getDefaultJvmTarget
import org.jetbrains.kotlin.resolve.TargetPlatform
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform

class KotlinGradleModuleConfigurator : KotlinWithGradleConfigurator() {

    override val name: String
        get() = NAME

    override val targetPlatform: TargetPlatform
        get() = JvmPlatform

    override val presentableText: String
        get() = "Java with Gradle"

    override val kotlinPluginName: String
        get() = KOTLIN

    override fun getKotlinPluginExpression(forKotlinDsl: Boolean): String =
        if (forKotlinDsl) "kotlin(\"jvm\")" else "id 'org.jetbrains.kotlin.jvm'"

    override fun getJvmTarget(sdk: Sdk?, version: String) = getDefaultJvmTarget(sdk, version)?.description

    override fun isApplicable(module: Module): Boolean {
        return super.isApplicable(module) && !module.isAndroidModule()
    }

    override fun configureModule(
        module: Module,
        file: PsiFile,
        isTopLevelProjectFile: Boolean,
        version: String, collector: NotificationMessageCollector,
        filesToOpen: MutableCollection<PsiFile>
    ) {
        super.configureModule(module, file, isTopLevelProjectFile, version, collector, filesToOpen)

        val moduleGroup = module.getWholeModuleGroup()
        for (sourceModule in moduleGroup.allModules()) {
            if (addStdlibToJavaModuleInfo(sourceModule, collector)) {
                break
            }
        }
    }

    companion object {
        val NAME = "gradle"
        val KOTLIN = "kotlin"
    }
}
