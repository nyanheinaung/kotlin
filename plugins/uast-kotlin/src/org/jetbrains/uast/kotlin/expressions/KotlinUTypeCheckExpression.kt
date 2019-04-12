/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.uast.UBinaryExpressionWithType
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UastBinaryExpressionWithTypeKind

class KotlinUTypeCheckExpression(
        override val psi: KtIsExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UBinaryExpressionWithType, KotlinUElementWithType, KotlinEvaluatableUElement {
    override val operand by lz { KotlinConverter.convertOrEmpty(psi.leftHandSide, this) }
    
    override val type by lz { psi.typeReference.toPsiType(this) }
    
    override val typeReference = psi.typeReference?.let {
        LazyKotlinUTypeReferenceExpression(it, this) { it.toPsiType(this) }
    }
    
    override val operationKind =
            if(psi.isNegated)
                KotlinBinaryExpressionWithTypeKinds.NEGATED_INSTANCE_CHECK
            else
                UastBinaryExpressionWithTypeKind.INSTANCE_CHECK
}