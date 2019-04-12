/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.uast.UCatchClause
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UParameter
import org.jetbrains.uast.UTypeReferenceExpression
import org.jetbrains.uast.kotlin.psi.UastKotlinPsiParameter

class KotlinUCatchClause(
        override val psi: KtCatchClause,
        givenParent: UElement?
) : KotlinAbstractUElement(givenParent), UCatchClause {

    override val javaPsi = null
    override val sourcePsi = psi

    override val body by lz { KotlinConverter.convertOrEmpty(psi.catchBody, this) }
    
    override val parameters by lz {
        val parameter = psi.catchParameter ?: return@lz emptyList<UParameter>()
        listOf(KotlinUParameter(UastKotlinPsiParameter.create(parameter, psi, this, 0), psi, this))
    }

    override val typeReferences by lz {
        val parameter = psi.catchParameter ?: return@lz emptyList<UTypeReferenceExpression>()
        val typeReference = parameter.typeReference ?: return@lz emptyList<UTypeReferenceExpression>()
        listOf(LazyKotlinUTypeReferenceExpression(typeReference, this) { typeReference.toPsiType(this, boxed = true) } )
    }
}