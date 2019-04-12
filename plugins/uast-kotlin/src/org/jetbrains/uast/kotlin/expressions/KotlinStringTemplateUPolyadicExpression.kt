/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UPolyadicExpression
import org.jetbrains.uast.UastBinaryOperator

class KotlinStringTemplateUPolyadicExpression(
        override val psi: KtStringTemplateExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent),
        UPolyadicExpression,
        KotlinUElementWithType,
        KotlinEvaluatableUElement {
    override val operands: List<UExpression> by lz { psi.entries.map { KotlinConverter.convertEntry(it, this)!! } }
    override val operator = UastBinaryOperator.PLUS
}