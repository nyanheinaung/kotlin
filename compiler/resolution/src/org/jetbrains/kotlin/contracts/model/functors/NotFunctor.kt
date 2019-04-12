/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.functors

import org.jetbrains.kotlin.contracts.model.ConditionalEffect
import org.jetbrains.kotlin.contracts.model.structure.ESConstants
import org.jetbrains.kotlin.contracts.model.structure.ESReturns
import org.jetbrains.kotlin.contracts.model.structure.isFalse
import org.jetbrains.kotlin.contracts.model.structure.isTrue

class NotFunctor(constants: ESConstants) : AbstractUnaryFunctor(constants) {
    override fun invokeWithReturningEffects(list: List<ConditionalEffect>): List<ConditionalEffect> = list.mapNotNull {
        val outcome = it.simpleEffect

        // Outcome guaranteed to be Returns by AbstractSequentialUnaryFunctor, but value
        // can be non-boolean in case of type-errors in the whole expression, like "foo(bar) && 1"
        val returnValue = (outcome as ESReturns).value

        when {
            returnValue.isTrue -> ConditionalEffect(it.condition, ESReturns(constants.falseValue))
            returnValue.isFalse -> ConditionalEffect(it.condition, ESReturns(constants.trueValue))
            else -> null
        }
    }
}
