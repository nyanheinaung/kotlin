/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UReturnExpression

class KotlinUReturnExpression(
        override val psi: KtReturnExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UReturnExpression, KotlinUElementWithType {
    override val returnExpression by lz { KotlinConverter.convertOrNull(psi.returnedExpression, this) }
}

class KotlinUImplicitReturnExpression(
    givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UReturnExpression, KotlinUElementWithType {
    override val psi: PsiElement?
        get() = null

    override lateinit var returnExpression: UExpression
        internal set

}