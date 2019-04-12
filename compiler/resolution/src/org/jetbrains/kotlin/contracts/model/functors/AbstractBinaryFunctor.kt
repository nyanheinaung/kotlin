/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.functors

import org.jetbrains.kotlin.contracts.model.Computation
import org.jetbrains.kotlin.contracts.model.ConditionalEffect
import org.jetbrains.kotlin.contracts.model.ESEffect
import org.jetbrains.kotlin.contracts.model.ESExpression
import org.jetbrains.kotlin.contracts.model.structure.*

abstract class AbstractBinaryFunctor(constants: ESConstants) : AbstractReducingFunctor(constants) {
    override fun doInvocation(arguments: List<Computation>): List<ESEffect> {
        assert(arguments.size == 2) { "Wrong size of arguments list for Binary functor: expected 2, got ${arguments.size}" }
        return invokeWithArguments(arguments[0], arguments[1])
    }

    fun invokeWithArguments(left: Computation, right: Computation): List<ESEffect> {
        if (left is ESConstant) return invokeWithConstant(right, left)
        if (right is ESConstant) return invokeWithConstant(left, right)

        val leftValueReturning =
            left.effects.filterIsInstance<ConditionalEffect>().filter { it.simpleEffect.isReturns { !value.isWildcard } }
        val rightValueReturning =
            right.effects.filterIsInstance<ConditionalEffect>().filter { it.simpleEffect.isReturns { !value.isWildcard } }

        val nonInterestingEffects =
            left.effects - leftValueReturning + right.effects - rightValueReturning

        val evaluatedByFunctor = invokeWithReturningEffects(leftValueReturning, rightValueReturning)

        return nonInterestingEffects + evaluatedByFunctor
    }

    protected fun foldConditionsWithOr(list: List<ConditionalEffect>): ESExpression? =
        if (list.isEmpty())
            null
        else
            list.map { it.condition }.reduce { acc, condition -> ESOr(constants, acc, condition) }

    protected abstract fun invokeWithConstant(computation: Computation, constant: ESConstant): List<ESEffect>

    protected abstract fun invokeWithReturningEffects(
        left: List<ConditionalEffect>,
        right: List<ConditionalEffect>
    ): List<ConditionalEffect>
}
