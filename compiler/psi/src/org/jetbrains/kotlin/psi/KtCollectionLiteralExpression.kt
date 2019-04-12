/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.lexer.KtTokens

class KtCollectionLiteralExpression(node: ASTNode) : KtExpressionImpl(node), KtReferenceExpression {
    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R {
        return visitor.visitCollectionLiteralExpression(this, data)
    }

    val leftBracket: PsiElement?
        get() = findChildByType(KtTokens.LBRACKET)

    val rightBracket: PsiElement?
        get() = findChildByType(KtTokens.RBRACKET)

    fun getInnerExpressions(): List<KtExpression> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KtExpression::class.java)
    }
}