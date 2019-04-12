/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.UTryExpression
import org.jetbrains.uast.UVariable
import org.jetbrains.uast.kotlin.declarations.KotlinUIdentifier

class KotlinUTryExpression(
        override val psi: KtTryExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UTryExpression, KotlinUElementWithType {
    override val tryClause by lz { KotlinConverter.convertOrEmpty(psi.tryBlock, this) }
    override val catchClauses by lz { psi.catchClauses.map { KotlinUCatchClause(it, this) } }
    override val finallyClause by lz { psi.finallyBlock?.finalExpression?.let { KotlinConverter.convertExpression(it, this) } }

    override val resourceVariables: List<UVariable>
        get() = emptyList()

    override val hasResources: Boolean
        get() = false

    override val tryIdentifier: UIdentifier
        get() = KotlinUIdentifier(null, this)

    override val finallyIdentifier: UIdentifier?
        get() = null
}