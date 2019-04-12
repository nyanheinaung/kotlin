/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class RemoveUselessIsCheckFixForWhen(element: KtWhenConditionIsPattern) : KotlinQuickFixAction<KtWhenConditionIsPattern>(element) {
    override fun getFamilyName() = "Remove useless is check"

    override fun getText(): String = familyName

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val condition = element ?: return
        val whenEntry = condition.parent as KtWhenEntry
        val whenExpression = whenEntry.parent as KtWhenExpression

        if (condition.isNegated) {
            condition.parent.delete()
        } else {
            whenExpression.entries.dropWhile { it != whenEntry }.forEach { it.delete() }
            val newEntry = KtPsiFactory(project).createWhenEntry("else -> ${whenEntry.expression!!.text}")
            whenExpression.addBefore(newEntry, whenExpression.closeBrace)
        }
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<KtWhenConditionIsPattern>? {
            val expression = diagnostic.psiElement.getNonStrictParentOfType<KtWhenConditionIsPattern>() ?: return null
            return RemoveUselessIsCheckFixForWhen(expression)
        }
    }
}
