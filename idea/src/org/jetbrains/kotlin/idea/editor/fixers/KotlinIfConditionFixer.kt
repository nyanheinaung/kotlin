/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor.fixers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtIfExpression

class KotlinIfConditionFixer : MissingConditionFixer<KtIfExpression>() {
    override val keyword = "if"
    override fun getElement(element: PsiElement?) = element as? KtIfExpression
    override fun getCondition(element: KtIfExpression) = element.condition
    override fun getLeftParenthesis(element: KtIfExpression) = element.leftParenthesis
    override fun getRightParenthesis(element: KtIfExpression) = element.rightParenthesis
    override fun getBody(element: KtIfExpression) = element.then
}
