/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiNamedElement
import com.intellij.psi.ResolveResult
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext.DOUBLE_COLON_LHS
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UMultiResolvable
import org.jetbrains.uast.kotlin.internal.getResolveResultVariants

class KotlinUCallableReferenceExpression(
        override val psi: KtCallableReferenceExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UCallableReferenceExpression, UMultiResolvable, KotlinUElementWithType {
    override val qualifierExpression: UExpression?
        get() {
            if (qualifierType != null) return null
            val receiverExpression = psi.receiverExpression ?: return null
            return KotlinConverter.convertExpression(receiverExpression, this)
        }

    override val qualifierType by lz {
        val ktType = psi.analyze()[DOUBLE_COLON_LHS, psi.receiverExpression]?.type ?: return@lz null
        ktType.toPsiType(this, psi, boxed = true)
    }

    override val callableName: String
        get() = psi.callableReference.getReferencedName()

    override val resolvedName: String?
        get() = (resolve() as? PsiNamedElement)?.name

    override fun resolve() = psi.callableReference.resolveCallToDeclaration()

    override fun multiResolve(): Iterable<ResolveResult> = getResolveResultVariants(psi.callableReference)

}