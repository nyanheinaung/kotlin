/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.project.NotUnderContentRootModuleInfo
import org.jetbrains.kotlin.idea.caches.project.getModuleInfo
import org.jetbrains.kotlin.idea.core.script.IdeScriptReportSink
import org.jetbrains.kotlin.idea.core.script.ScriptDefinitionsManager
import org.jetbrains.kotlin.idea.core.script.ScriptDependenciesUpdater
import org.jetbrains.kotlin.idea.util.ProjectRootsUtil
import org.jetbrains.kotlin.idea.util.isRunningInCidrIde
import org.jetbrains.kotlin.psi.KtCodeFragment
import org.jetbrains.kotlin.psi.KtFile
import kotlin.script.experimental.dependencies.ScriptReport

object KotlinHighlightingUtil {
    fun shouldHighlight(psiElement: PsiElement): Boolean {
        val ktFile = psiElement.containingFile as? KtFile ?: return false

        if (ktFile is KtCodeFragment && ktFile.context != null) {
            return true
        }

        if (ktFile.isScript()) {
            return shouldHighlightScript(ktFile)
        }

        if (OutsidersPsiFileSupportWrapper.isOutsiderFile(ktFile.virtualFile)) {
            return true
        }

        return ProjectRootsUtil.isInProjectOrLibraryContent(ktFile) && ktFile.getModuleInfo() !is NotUnderContentRootModuleInfo
    }

    fun shouldHighlightErrors(psiElement: PsiElement): Boolean {
        val ktFile = psiElement.containingFile as? KtFile ?: return false
        if (ktFile.isCompiled) {
            return false
        }
        if (ktFile is KtCodeFragment && ktFile.context != null) {
            return true
        }

        if (ktFile.isScript()) {
            return shouldHighlightScript(ktFile)
        }

        return ProjectRootsUtil.isInProjectSource(ktFile)
    }


    @Suppress("DEPRECATION")
    private fun shouldHighlightScript(ktFile: KtFile): Boolean {
        if (isRunningInCidrIde) return false // There is no Java support in CIDR. So do not highlight errors in KTS if running in CIDR.
        if (!ScriptDependenciesUpdater.areDependenciesCached(ktFile)) return false
        if (ktFile.virtualFile.getUserData(IdeScriptReportSink.Reports)?.any { it.severity == ScriptReport.Severity.FATAL } == true) {
            return false
        }

        if (!ScriptDefinitionsManager.getInstance(ktFile.project).isReady()) return false

        return ProjectRootsUtil.isInProjectSource(ktFile, includeScriptsOutsideSourceRoots = true)
    }
}