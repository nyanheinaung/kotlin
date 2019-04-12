/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ResolveScopeProvider
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.idea.core.script.ScriptDependenciesManager
import org.jetbrains.kotlin.idea.core.script.StandardIdeScriptDefinition
import org.jetbrains.kotlin.script.KotlinScriptDefinitionFromAnnotatedTemplate
import org.jetbrains.kotlin.scripting.shared.definitions.findScriptDefinition

class KotlinScriptResolveScopeProvider : ResolveScopeProvider() {
    companion object {
        // Used in LivePlugin
        val USE_NULL_RESOLVE_SCOPE = "USE_NULL_RESOLVE_SCOPE"
    }

    override fun getResolveScope(file: VirtualFile, project: Project): GlobalSearchScope? {
        val scriptDefinition = file.findScriptDefinition(project)
        // TODO: this should get this particular scripts dependencies
        return when {
            scriptDefinition == null -> null
        // This is a workaround for completion in scripts and REPL to provide module dependencies
            scriptDefinition.template == Any::class -> null
            scriptDefinition is StandardIdeScriptDefinition -> null
            scriptDefinition is KotlinScriptDefinitionFromAnnotatedTemplate -> // TODO: should include the file itself
                ScriptDependenciesManager.getInstance(project).getAllScriptsClasspathScope()
            else -> null
        }
    }
}