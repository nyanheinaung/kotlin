/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.DelegatingGlobalSearchScope
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.storage.StorageManager
import java.util.*

abstract class DeclarationProviderFactoryService {

    abstract fun create(
        project: Project,
        storageManager: StorageManager,
        syntheticFiles: Collection<KtFile>,
        filesScope: GlobalSearchScope,
        moduleInfo: ModuleInfo
    ): DeclarationProviderFactory

    companion object {
        @JvmStatic
        fun createDeclarationProviderFactory(
            project: Project,
            storageManager: StorageManager,
            syntheticFiles: Collection<KtFile>,
            moduleContentScope: GlobalSearchScope,
            moduleInfo: ModuleInfo
        ): DeclarationProviderFactory {
            return ServiceManager.getService(project, DeclarationProviderFactoryService::class.java)!!
                .create(project, storageManager, syntheticFiles, filteringScope(syntheticFiles, moduleContentScope), moduleInfo)
        }

        private fun filteringScope(syntheticFiles: Collection<KtFile>, baseScope: GlobalSearchScope): GlobalSearchScope {
            if (syntheticFiles.isEmpty() || baseScope == GlobalSearchScope.EMPTY_SCOPE) {
                return baseScope
            }
            return SyntheticFilesFilteringScope(syntheticFiles, baseScope)
        }
    }


    private class SyntheticFilesFilteringScope(syntheticFiles: Collection<KtFile>, baseScope: GlobalSearchScope) :
        DelegatingGlobalSearchScope(baseScope) {

        private val originals = syntheticFiles.mapNotNullTo(HashSet<VirtualFile>()) { it.originalFile.virtualFile }

        override fun contains(file: VirtualFile) = super.contains(file) && file !in originals

        override fun toString() = "SyntheticFilesFilteringScope($myBaseScope)"
    }
}
