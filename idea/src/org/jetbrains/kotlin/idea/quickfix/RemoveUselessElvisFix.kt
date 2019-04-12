/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.core.dropEnclosingParenthesesIfPossible
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtFile

class RemoveUselessElvisFix(element: KtBinaryExpression) : KotlinQuickFixAction<KtBinaryExpression>(element), CleanupFix {
    override fun getFamilyName(): String = "Remove useless elvis operator"

    override fun getText(): String = familyName

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        dropEnclosingParenthesesIfPossible(element.replaced(element.left!!))
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<KtBinaryExpression>? {
            val expression = diagnostic.psiElement as? KtBinaryExpression ?: return null
            return RemoveUselessElvisFix(expression)
        }
    }
}