/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class AddRunToLambdaFix(element: KtLambdaExpression) : KotlinQuickFixAction<KtLambdaExpression>(element) {
    override fun getText() = "Add 'run' before the lambda expression"
    override fun getFamilyName() = text

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        val factory = KtPsiFactory(project)
        element.replace(factory.createExpression("run ${element.text}"))
    }

    companion object Factory : KotlinIntentionActionsFactory() {
        override fun doCreateActions(diagnostic: Diagnostic): List<IntentionAction> {
            val casted = Errors.UNUSED_LAMBDA_EXPRESSION.cast(diagnostic)
            return listOf(AddRunToLambdaFix(casted.psiElement))
        }
    }
}