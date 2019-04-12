/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.lexer.KtTokens

internal fun KtElement.deleteSemicolon() {
    if (this is KtEnumEntry) return

    val sibling = PsiTreeUtil.skipSiblingsForward(this, PsiWhiteSpace::class.java, PsiComment::class.java)
    if (sibling == null || sibling.node.elementType != KtTokens.SEMICOLON) return

    val lastSiblingToDelete = PsiTreeUtil.skipSiblingsForward(sibling, PsiWhiteSpace::class.java)?.prevSibling ?: sibling
    parent.deleteChildRange(nextSibling, lastSiblingToDelete)
}

fun KtExpression.unpackFunctionLiteral(allowParentheses: Boolean = false): KtLambdaExpression? {
    return when (this) {
        is KtLambdaExpression -> this
        is KtLabeledExpression -> baseExpression?.unpackFunctionLiteral(allowParentheses)
        is KtAnnotatedExpression -> baseExpression?.unpackFunctionLiteral(allowParentheses)
        is KtParenthesizedExpression -> if (allowParentheses) expression?.unpackFunctionLiteral(allowParentheses) else null
        else -> null
    }
}