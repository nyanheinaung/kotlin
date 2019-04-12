/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.UPrefixExpression
import org.jetbrains.uast.UastPrefixOperator
import org.jetbrains.uast.kotlin.declarations.KotlinUIdentifier

class KotlinUPrefixExpression(
        override val psi: KtPrefixExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UPrefixExpression, KotlinUElementWithType, KotlinEvaluatableUElement {
    override val operand by lz { KotlinConverter.convertOrEmpty(psi.baseExpression, this) }

    override val operatorIdentifier: UIdentifier?
        get() = KotlinUIdentifier(psi.operationReference, this)

    override fun resolveOperator() = psi.operationReference.resolveCallToDeclaration() as? PsiMethod

    override val operator = when (psi.operationToken) {
        KtTokens.EXCL -> UastPrefixOperator.LOGICAL_NOT
        KtTokens.PLUS -> UastPrefixOperator.UNARY_PLUS
        KtTokens.MINUS -> UastPrefixOperator.UNARY_MINUS
        KtTokens.PLUSPLUS -> UastPrefixOperator.INC
        KtTokens.MINUSMINUS -> UastPrefixOperator.DEC
        else -> UastPrefixOperator.UNKNOWN
    }
}