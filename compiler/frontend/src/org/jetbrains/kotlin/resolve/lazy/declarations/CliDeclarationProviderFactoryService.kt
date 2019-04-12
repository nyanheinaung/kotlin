/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.utils.sure
import java.util.ArrayList

class CliDeclarationProviderFactoryService(private val sourceFiles: Collection<KtFile>) : DeclarationProviderFactoryService() {

    override fun create(
        project: Project,
        storageManager: StorageManager,
        syntheticFiles: Collection<KtFile>,
        filesScope: GlobalSearchScope,
        moduleInfo: ModuleInfo
    ): DeclarationProviderFactory {
        val allFiles = ArrayList<KtFile>()
        sourceFiles.filterTo(allFiles) {
            val vFile = it.virtualFile.sure { "Source files should be physical files" }
            filesScope.contains(vFile)
        }
        allFiles.addAll(syntheticFiles)
        return FileBasedDeclarationProviderFactory(storageManager, allFiles)
    }
}
