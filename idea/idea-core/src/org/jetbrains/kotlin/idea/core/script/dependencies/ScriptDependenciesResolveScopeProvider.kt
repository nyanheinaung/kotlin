/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ResolveScopeProvider
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.idea.caches.project.ScriptDependenciesInfo
import org.jetbrains.kotlin.idea.caches.project.ScriptDependenciesSourceInfo
import org.jetbrains.kotlin.idea.caches.project.getModuleInfoByVirtualFile

class ScriptDependenciesResolveScopeProvider : ResolveScopeProvider() {
    override fun getResolveScope(file: VirtualFile, project: Project): GlobalSearchScope? {
        val moduleInfo = getModuleInfoByVirtualFile(project, file) ?: return null
        val scriptDependenciesModuleInfo = (moduleInfo as? ScriptDependenciesInfo)
                                           ?: (moduleInfo as? ScriptDependenciesSourceInfo)?.binariesModuleInfo
                                           ?: return null
        return GlobalSearchScope.union(
                arrayOf(
                        GlobalSearchScope.fileScope(project, file),
                        *scriptDependenciesModuleInfo.dependencies().map { it.contentScope() }.toTypedArray()
                )
        )
    }
}