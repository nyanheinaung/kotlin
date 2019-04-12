/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.IndexableSetContributor
import org.jetbrains.kotlin.idea.core.script.ScriptDependenciesManager

class KotlinScriptDependenciesIndexableSetContributor : IndexableSetContributor() {

    override fun getAdditionalProjectRootsToIndex(project: Project): Set<VirtualFile> {
        val manager = ScriptDependenciesManager.getInstance(project)
        return (manager.getAllScriptsClasspath() + manager.getAllLibrarySources()).filterTo(LinkedHashSet()) { it.isValid }
    }

    override fun getAdditionalRootsToIndex(): Set<VirtualFile> = emptySet()
}