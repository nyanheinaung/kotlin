/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin.expressions

import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.uast.UBreakExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.kotlin.KotlinAbstractUExpression

class KotlinUBreakExpression(
        override val psi: KtBreakExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UBreakExpression {
    override val label: String?
        get() = psi.getLabelName()
}