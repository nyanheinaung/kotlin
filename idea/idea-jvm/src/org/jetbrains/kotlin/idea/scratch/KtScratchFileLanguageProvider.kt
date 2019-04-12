/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.scratch

import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.scratch.compile.KtCompilingExecutor
import org.jetbrains.kotlin.idea.scratch.output.InlayScratchOutputHandler
import org.jetbrains.kotlin.idea.scratch.repl.KtScratchReplExecutor
import org.jetbrains.kotlin.psi.KtFile

class KtScratchFileLanguageProvider : ScratchFileLanguageProvider() {
    override fun createFile(project: Project, editor: TextEditor): ScratchFile? {
        return KtScratchFile(project, editor)
    }

    override fun createReplExecutor(file: ScratchFile) = KtScratchReplExecutor(file)
    override fun createCompilingExecutor(file: ScratchFile) = KtCompilingExecutor(file)

    override fun getOutputHandler() = InlayScratchOutputHandler
}