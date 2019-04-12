/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.uast.*
import org.jetbrains.uast.kotlin.declarations.KotlinUIdentifier
import org.jetbrains.uast.kotlin.internal.DelegatedMultiResolve

class KotlinUPostfixExpression(
        override val psi: KtPostfixExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UPostfixExpression, KotlinUElementWithType, KotlinEvaluatableUElement,
    UResolvable, DelegatedMultiResolve {
    override val operand by lz { KotlinConverter.convertOrEmpty(psi.baseExpression, this) }

    override val operator = when (psi.operationToken) {
        KtTokens.PLUSPLUS -> UastPostfixOperator.INC
        KtTokens.MINUSMINUS -> UastPostfixOperator.DEC
        KtTokens.EXCLEXCL -> KotlinPostfixOperators.EXCLEXCL
        else -> UastPostfixOperator.UNKNOWN
    }

    override val operatorIdentifier: UIdentifier?
        get() = KotlinUIdentifier(psi.operationReference, this)

    override fun resolveOperator() = psi.operationReference.resolveCallToDeclaration() as? PsiMethod

    override fun resolve(): PsiMethod? = when (psi.operationToken) {
        KtTokens.EXCLEXCL -> operand.tryResolve() as? PsiMethod
        else -> null
    }
}