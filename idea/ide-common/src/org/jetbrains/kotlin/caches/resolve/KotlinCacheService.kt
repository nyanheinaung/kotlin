/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.caches.resolve

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.TargetPlatform
import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache

interface KotlinCacheService {
    companion object {
        fun getInstance(project: Project): KotlinCacheService = ServiceManager.getService(project, KotlinCacheService::class.java)!!
    }

    fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade
    fun getResolutionFacadeByFile(file: PsiFile, platform: TargetPlatform): ResolutionFacade?

    fun getSuppressionCache(): KotlinSuppressCache
    fun getResolutionFacadeByModuleInfo(moduleInfo: ModuleInfo, platform: TargetPlatform): ResolutionFacade?
}