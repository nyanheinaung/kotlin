/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiType
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.kotlin.psi.UastKotlinPsiParameter
import org.jetbrains.uast.withMargin

class KotlinULambdaExpression(
        override val psi: KtLambdaExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), ULambdaExpression, KotlinUElementWithType {
    override val functionalInterfaceType: PsiType?
        get() = getFunctionalInterfaceType()

    override val body by lz { KotlinConverter.convertOrEmpty(psi.bodyExpression, this) }
    
    override val valueParameters by lz {
        psi.valueParameters.mapIndexed { i, p ->
            KotlinUParameter(UastKotlinPsiParameter.create(p, psi, this, i), psi, this)
        }
    }
    
    override fun asRenderString(): String {
        val renderedValueParameters = if (valueParameters.isEmpty())
            ""
        else
            valueParameters.joinToString { it.asRenderString() } + " ->\n"
        val expressions = (body as? UBlockExpression)?.expressions
                                  ?.joinToString("\n") { it.asRenderString().withMargin } ?: body.asRenderString()

        return "{ $renderedValueParameters\n$expressions\n}"
    }
}