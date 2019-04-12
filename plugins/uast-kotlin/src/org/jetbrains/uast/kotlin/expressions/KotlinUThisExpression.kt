/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.UThisExpression
import org.jetbrains.uast.kotlin.declarations.KotlinUIdentifier
import org.jetbrains.uast.kotlin.internal.DelegatedMultiResolve

class KotlinUThisExpression(
        override val psi: KtThisExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UThisExpression, DelegatedMultiResolve, KotlinUElementWithType, KotlinEvaluatableUElement {
    override val label: String?
        get() = psi.getLabelName()

    override val labelIdentifier: UIdentifier?
        get() = psi.getTargetLabel()?.let { KotlinUIdentifier(it, this) }

    override fun resolve() = psi.analyze()[BindingContext.LABEL_TARGET, psi.getTargetLabel()]
}