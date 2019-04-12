/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.uast.UArrayAccessExpression
import org.jetbrains.uast.UElement

class KotlinUArrayAccessExpression(
        override val psi: KtArrayAccessExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UArrayAccessExpression, KotlinUElementWithType, KotlinEvaluatableUElement {
    override val receiver by lz { KotlinConverter.convertOrEmpty(psi.arrayExpression, this) }
    override val indices by lz { psi.indexExpressions.map { KotlinConverter.convertOrEmpty(it, this) } }
}