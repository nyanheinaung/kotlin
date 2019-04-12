/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.resolve.BindingContext.DOUBLE_COLON_LHS
import org.jetbrains.uast.UClassLiteralExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression

class KotlinUClassLiteralExpression(
        override val psi: KtClassLiteralExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UClassLiteralExpression, KotlinUElementWithType {
    override val type by lz {
        val ktType = psi.analyze()[DOUBLE_COLON_LHS, psi.receiverExpression]?.type ?: return@lz null
        ktType.toPsiType(this, psi, boxed = true)
    }
    
    override val expression: UExpression?
        get() {
            if (type != null) return null
            val receiverExpression = psi.receiverExpression ?: return null
            return KotlinConverter.convertExpression(receiverExpression, this)
        }
}