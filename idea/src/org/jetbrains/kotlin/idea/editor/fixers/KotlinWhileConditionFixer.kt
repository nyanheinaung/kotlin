/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor.fixers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtWhileExpression

class KotlinWhileConditionFixer : MissingConditionFixer<KtWhileExpression>() {
    override val keyword = "while"
    override fun getElement(element: PsiElement?) = element as? KtWhileExpression
    override fun getCondition(element: KtWhileExpression) = element.condition
    override fun getLeftParenthesis(element: KtWhileExpression) = element.leftParenthesis
    override fun getRightParenthesis(element: KtWhileExpression) = element.rightParenthesis
    override fun getBody(element: KtWhileExpression) = element.body
}
