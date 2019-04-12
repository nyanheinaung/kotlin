/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin.expressions

import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.uast.UContinueExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.kotlin.KotlinAbstractUExpression

class KotlinUContinueExpression(
        override val psi: KtContinueExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UContinueExpression {
    override val label: String?
        get() = psi.getLabelName()
}