/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UForEachExpression
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.kotlin.declarations.KotlinUIdentifier
import org.jetbrains.uast.kotlin.psi.UastKotlinPsiParameter
import org.jetbrains.uast.psi.UastPsiParameterNotResolved

class KotlinUForEachExpression(
        override val psi: KtForExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UForEachExpression {
    override val iteratedValue by lz { KotlinConverter.convertOrEmpty(psi.loopRange, this) }
    override val body by lz { KotlinConverter.convertOrEmpty(psi.body, this) }
    
    override val variable by lz {
        val parameter = psi.loopParameter?.let { UastKotlinPsiParameter.create(it, psi, this, 0) } 
                ?: UastPsiParameterNotResolved(psi, KotlinLanguage.INSTANCE)
        KotlinUParameter(parameter, psi, this)
    }

    override val forIdentifier: UIdentifier
        get() = KotlinUIdentifier(null, this)
}