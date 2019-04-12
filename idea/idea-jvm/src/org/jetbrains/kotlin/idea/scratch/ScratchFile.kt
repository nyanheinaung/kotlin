/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.scratch

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.scratch.ui.scratchFileOptions

abstract class ScratchFile(val project: Project, val editor: TextEditor) {
    fun getExpressions(): List<ScratchExpression> = runReadAction {
        getPsiFile()?.let { getExpressions(it) } ?: emptyList()
    }

    fun getPsiFile(): PsiFile? = runReadAction {
        PsiDocumentManager.getInstance(project).getPsiFile(editor.editor.document)
    }

    fun getModule(): Module? {
        return editor.getScratchPanel()?.getModule()
    }

    val options: ScratchFileOptions
        get() = getPsiFile()?.virtualFile?.scratchFileOptions ?: ScratchFileOptions()

    fun saveOptions(update: ScratchFileOptions.() -> ScratchFileOptions) {
        val virtualFile = getPsiFile()?.virtualFile ?: return
        with(virtualFile) {
            val configToUpdate = scratchFileOptions ?: ScratchFileOptions()
            scratchFileOptions = configToUpdate.update()
        }
    }

    abstract fun getExpressions(psiFile: PsiFile): List<ScratchExpression>
    abstract fun hasErrors(): Boolean
}

data class ScratchExpression(val element: PsiElement, val lineStart: Int, val lineEnd: Int = lineStart)

data class ScratchFileOptions(
    val isRepl: Boolean = false,
    val isMakeBeforeRun: Boolean = false,
    val isInteractiveMode: Boolean = true
)