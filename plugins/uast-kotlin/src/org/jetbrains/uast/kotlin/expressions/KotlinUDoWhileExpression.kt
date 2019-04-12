/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.uast.UDoWhileExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.kotlin.declarations.KotlinUIdentifier

class KotlinUDoWhileExpression(
        override val psi: KtDoWhileExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), UDoWhileExpression {
    override val condition by lz { KotlinConverter.convertOrEmpty(psi.condition, this) }
    override val body by lz { KotlinConverter.convertOrEmpty(psi.body, this) }

    override val doIdentifier: UIdentifier
        get() = KotlinUIdentifier(null, this)

    override val whileIdentifier: UIdentifier
        get() = KotlinUIdentifier(null, this)
}