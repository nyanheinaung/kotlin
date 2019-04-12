/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.UIfExpression

class KotlinUIfExpression(
        override val psi: KtIfExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UIfExpression, KotlinUElementWithType, KotlinEvaluatableUElement {
    override val condition by lz { KotlinConverter.convertOrEmpty(psi.condition, this) }
    override val thenExpression by lz { KotlinConverter.convertOrNull(psi.then, this) }
    override val elseExpression by lz { KotlinConverter.convertOrNull(psi.`else`, this) }
    override val isTernary = false

    override val ifIdentifier: UIdentifier
        get() = UIdentifier(null, this)

    override val elseIdentifier: UIdentifier?
        get() = null
}