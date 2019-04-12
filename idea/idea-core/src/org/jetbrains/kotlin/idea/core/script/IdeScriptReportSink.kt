/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotifications
import org.jetbrains.kotlin.script.ScriptReportSink
import kotlin.script.experimental.dependencies.ScriptReport

class IdeScriptReportSink(val project: Project) : ScriptReportSink {
    override fun attachReports(scriptFile: VirtualFile, reports: List<ScriptReport>) {
        // TODO: persist errors between launches?
        scriptFile.putUserData(Reports, reports)

        ApplicationManager.getApplication().invokeLater {
            if (scriptFile.isValid && !project.isDisposed) {
                PsiManager.getInstance(project).findFile(scriptFile)?.let { psiFile ->
                    DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
                    EditorNotifications.getInstance(project).updateNotifications(scriptFile)
                }
            }
        }
    }

    object Reports : Key<List<ScriptReport>>("KOTLIN_SCRIPT_REPORTS")
}