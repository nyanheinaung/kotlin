/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.analyzer.LanguageSettingsProvider
import org.jetbrains.kotlin.analyzer.ModuleContent
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.analyzer.ResolverForProjectImpl
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.load.kotlin.PackagePartProvider
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.jvm.JvmAnalyzerFacade
import org.jetbrains.kotlin.resolve.jvm.JvmPlatformParameters

fun createResolveSessionForFiles(
        project: Project,
        syntheticFiles: Collection<KtFile>,
        addBuiltIns: Boolean
): ResolveSession {
    val projectContext = ProjectContext(project)
    val testModule = TestModule(addBuiltIns)
    val resolverForProject = ResolverForProjectImpl(
        "test",
        projectContext, listOf(testModule),
        { ModuleContent(it, syntheticFiles, GlobalSearchScope.allScope(project)) },
        moduleLanguageSettingsProvider = LanguageSettingsProvider.Default,
        resolverForModuleFactoryByPlatform = { JvmAnalyzerFacade },
        platformParameters = { _ ->
            JvmPlatformParameters(
                packagePartProviderFactory = { PackagePartProvider.Empty },
                moduleByJavaClass = { testModule }
            )
        }
    )
    return resolverForProject.resolverForModule(testModule).componentProvider.get<ResolveSession>()
}

private class TestModule(val dependsOnBuiltIns: Boolean) : ModuleInfo {
    override val name: Name = Name.special("<Test module for lazy resolve>")
    override fun dependencies() = listOf(this)
    override fun dependencyOnBuiltIns() =
            if (dependsOnBuiltIns)
                ModuleInfo.DependencyOnBuiltIns.LAST
            else
                ModuleInfo.DependencyOnBuiltIns.NONE
}
