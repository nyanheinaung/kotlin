/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UTypeReferenceExpression

open class KotlinUTypeReferenceExpression(
        override val type: PsiType,
        override val psi: PsiElement?,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UTypeReferenceExpression, KotlinUElementWithType


class LazyKotlinUTypeReferenceExpression(
        override val psi: KtTypeReference,
        givenParent: UElement?,
        private val typeSupplier: (() -> PsiType)? = null
) : KotlinAbstractUExpression(givenParent), UTypeReferenceExpression {
    override val type: PsiType by lz {
        typeSupplier?.invoke() ?: psi.toPsiType(uastParent ?: this)
    }
}
