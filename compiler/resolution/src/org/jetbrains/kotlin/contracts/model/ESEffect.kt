/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model

sealed class ESEffect {
    /**
     * Returns:
     *  - true, when presence of `this`-effect necessary implies presence of `other`-effect
     *  - false, when presence of `this`-effect necessary implies absence of `other`-effect
     *  - null, when presence of `this`-effect doesn't implies neither presence nor absence of `other`-effect
     */
    abstract fun isImplies(other: ESEffect): Boolean?
}

/**
 * Abstraction of some side-effect of a computation.
 *
 * SimpleEffect alone means that this effect will definitely be fired.
 */
abstract class SimpleEffect : ESEffect()

/**
 * Effect with condition attached to it.
 *
 * Has the same semantics as [org.jetbrains.kotlin.contracts.description.ConditionalEffectDeclaration]
 */
class ConditionalEffect(val condition: ESExpression, val simpleEffect: SimpleEffect) : ESEffect() {
    // Conservatively, always return null, indicating absence of information
    override fun isImplies(other: ESEffect): Boolean? = null
}



