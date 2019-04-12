/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression

class KotlinULiteralExpression(
        override val psi: KtConstantExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), ULiteralExpression, KotlinUElementWithType, KotlinEvaluatableUElement {
    override val isNull: Boolean
        get() = psi.unwrapBlockOrParenthesis().node?.elementType == KtNodeTypes.NULL

    override val value by lz { evaluate() }
}

class KotlinStringULiteralExpression(
        override val psi: PsiElement,
        givenParent: UElement?,
        val text: String
) : KotlinAbstractUExpression(givenParent), ULiteralExpression, KotlinUElementWithType{
    constructor(psi: PsiElement, uastParent: UElement?)
            : this(psi, uastParent, if (psi is KtEscapeStringTemplateEntry) psi.unescapedValue else psi.text)

    override val value: String
        get() = text

    override fun evaluate() = value

    override fun getExpressionType(): PsiType? = PsiType.getJavaLangString(psi.manager, psi.resolveScope)
}
