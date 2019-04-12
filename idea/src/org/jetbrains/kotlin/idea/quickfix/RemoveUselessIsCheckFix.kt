/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class RemoveUselessIsCheckFix(element: KtIsExpression) : KotlinQuickFixAction<KtIsExpression>(element) {
    override fun getFamilyName() = "Remove useless is check"

    override fun getText(): String = familyName

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        element?.run {
            val expressionsText = this.isNegated.not().toString()
            val newExpression = KtPsiFactory(project).createExpression(expressionsText)
            replace(newExpression)
        }
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<KtIsExpression>? {
            val expression = diagnostic.psiElement.getNonStrictParentOfType<KtIsExpression>() ?: return null
            return RemoveUselessIsCheckFix(expression)
        }
    }
}
