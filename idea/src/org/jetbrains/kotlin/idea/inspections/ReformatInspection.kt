/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInsight.actions.FormatChangedTextUtil
import com.intellij.codeInspection.*
import com.intellij.codeInspection.ex.ProblemDescriptorImpl
import com.intellij.codeInspection.ui.SingleCheckboxOptionsPanel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.formatter.FormattingChange
import org.jetbrains.kotlin.idea.formatter.FormattingChange.ReplaceWhiteSpace
import org.jetbrains.kotlin.idea.formatter.FormattingChange.ShiftIndentInsideRange
import org.jetbrains.kotlin.idea.formatter.collectFormattingChanges
import org.jetbrains.kotlin.idea.util.ProjectRootsUtil
import org.jetbrains.kotlin.psi.KtFile
import javax.swing.JComponent
import javax.xml.bind.annotation.XmlAttribute

class ReformatInspection : LocalInspectionTool() {
    @XmlAttribute
    var processChangedFilesOnly: Boolean = false

    override fun runForWholeFile(): Boolean = true

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<out ProblemDescriptor>? {
        return checkFile(file, isOnTheFly)?.toTypedArray()
    }

    private fun checkFile(file: PsiFile, isOnTheFly: Boolean): List<ProblemDescriptor>? {
        if (file !is KtFile || !file.isWritable || !ProjectRootsUtil.isInProjectSource(file)) {
            return null
        }

        if (processChangedFilesOnly && !FormatChangedTextUtil.hasChanges(file)) {
            return null
        }

        val changes = collectFormattingChanges(file)
        if (changes.isEmpty()) return null

        val elements = changes.asSequence().map {
            val rangeOffset = when (it) {
                is ShiftIndentInsideRange -> it.range.startOffset
                is ReplaceWhiteSpace -> it.textRange.startOffset
            }

            val leaf = file.findElementAt(rangeOffset) ?: return@map null
            if (!leaf.isValid) return@map null
            if (leaf is PsiWhiteSpace && isEmptyLineReformat(leaf, it)) return@map null

            leaf
        }.filterNotNull().toList()

        return elements.map {
            ProblemDescriptorImpl(
                it, it,
                "File is not properly formatted",
                arrayOf(ReformatQuickFix),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false, null,
                isOnTheFly
            )
        }
    }

    override fun createOptionsPanel(): JComponent? {
        return SingleCheckboxOptionsPanel(
            "Apply only to modified files (for projects under a version control)",
            this,
            "processChangedFilesOnly"
        )
    }

    private fun isEmptyLineReformat(whitespace: PsiWhiteSpace, change: FormattingChange): Boolean {
        if (change !is ReplaceWhiteSpace) return false

        val beforeText = whitespace.text
        val afterText = change.whiteSpace

        return beforeText.count { it == '\n' } == afterText.count { it == '\n' } &&
                beforeText.substringAfterLast('\n') == afterText.substringAfterLast('\n')
    }

    private object ReformatQuickFix : LocalQuickFix {
        override fun getFamilyName(): String = "Reformat File"
        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            CodeStyleManager.getInstance(project).reformat(descriptor.psiElement.containingFile)
        }
    }
}