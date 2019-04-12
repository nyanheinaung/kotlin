/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.inline

import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.intellij.refactoring.JavaRefactoringSettings
import org.jetbrains.kotlin.idea.codeInliner.UsageReplacementStrategy
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtProperty

class KotlinInlineValDialog(
    property: KtProperty,
    reference: KtSimpleNameReference?,
    private val replacementStrategy: UsageReplacementStrategy,
    private val assignmentToDelete: KtBinaryExpression?,
    withPreview: Boolean = true
) : AbstractKotlinInlineDialog(property, reference) {

    private val isLocal = (callable as KtProperty).isLocal

    private val simpleLocal = isLocal && (reference == null || occurrencesNumber == 1)

    init {
        setPreviewResults(withPreview && shouldBeShown())
        if (simpleLocal) {
            setDoNotAskOption(object : DoNotAskOption {
                override fun isToBeShown() = EditorSettingsExternalizable.getInstance().isShowInlineLocalDialog

                override fun setToBeShown(value: Boolean, exitCode: Int) {
                    EditorSettingsExternalizable.getInstance().isShowInlineLocalDialog = value
                }

                override fun canBeHidden() = true

                override fun shouldSaveOptionsOnCancel() = false

                override fun getDoNotShowMessage() = "Do not show for local variables in future"
            })
        }
        init()
    }

    fun shouldBeShown() = !simpleLocal || EditorSettingsExternalizable.getInstance().isShowInlineLocalDialog

    override fun isInlineThis() = JavaRefactoringSettings.getInstance().INLINE_LOCAL_THIS

    public override fun doAction() {
        invokeRefactoring(
            KotlinInlineCallableProcessor(
                project, replacementStrategy, callable, reference,
                inlineThisOnly = isInlineThisOnly,
                deleteAfter = !isInlineThisOnly && !isKeepTheDeclaration,
                statementToDelete = assignmentToDelete
            )
        )

        val settings = JavaRefactoringSettings.getInstance()
        if (myRbInlineThisOnly.isEnabled && myRbInlineAll.isEnabled) {
            settings.INLINE_LOCAL_THIS = isInlineThisOnly
        }
    }
}
