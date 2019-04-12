/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiNamedElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UastQualifiedExpressionAccessType
import org.jetbrains.uast.kotlin.internal.DelegatedMultiResolve

class KotlinUQualifiedReferenceExpression(
        override val psi: KtDotQualifiedExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UQualifiedReferenceExpression, DelegatedMultiResolve,
        KotlinUElementWithType, KotlinEvaluatableUElement {
    override val receiver by lz { KotlinConverter.convertOrEmpty(psi.receiverExpression, this) }
    override val selector by lz { KotlinConverter.convertOrEmpty(psi.selectorExpression, this) }
    override val accessType = UastQualifiedExpressionAccessType.SIMPLE

    override fun resolve() = psi.selectorExpression?.resolveCallToDeclaration()

    override val resolvedName: String?
        get() = (resolve() as? PsiNamedElement)?.name
}
