/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.scratch.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.scratch.ScratchFileLanguageProvider
import org.jetbrains.kotlin.idea.scratch.getScratchPanelFromSelectedEditor

class ClearScratchAction : ScratchAction(
    KotlinBundle.message("scratch.clear.button"),
    AllIcons.Actions.GC
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val scratchFile = getScratchPanelFromSelectedEditor(project)?.scratchFile ?: return
        val psiFile = scratchFile.getPsiFile() ?: return

        ScratchFileLanguageProvider.get(psiFile.language)?.getOutputHandler()?.clear(scratchFile)
    }
}