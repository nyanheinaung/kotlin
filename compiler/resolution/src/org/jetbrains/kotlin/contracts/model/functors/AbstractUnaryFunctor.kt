/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.functors

import org.jetbrains.kotlin.contracts.model.Computation
import org.jetbrains.kotlin.contracts.model.ConditionalEffect
import org.jetbrains.kotlin.contracts.model.ESEffect
import org.jetbrains.kotlin.contracts.model.structure.ESConstants
import org.jetbrains.kotlin.contracts.model.structure.isReturns
import org.jetbrains.kotlin.contracts.model.structure.isWildcard

/**
 * Unary functor that has sequential semantics, i.e. it won't apply to
 * computations that can't be guaranteed to be finished.
 *
 * It provides [applyToFinishingClauses] method for successors, which is guaranteed to
 * be called only on clauses that haven't failed before reaching functor transformation.
 */
abstract class AbstractUnaryFunctor(constants: ESConstants) : AbstractReducingFunctor(constants) {
    override fun doInvocation(arguments: List<Computation>): List<ESEffect> {
        assert(arguments.size == 1) { "Wrong size of arguments list for Unary operator: expected 1, got ${arguments.size}" }
        return invokeWithArguments(arguments[0])
    }

    fun invokeWithArguments(arg: Computation): List<ESEffect> {
        val returning =
            arg.effects.filterIsInstance<ConditionalEffect>().filter { it.simpleEffect.isReturns { !value.isWildcard } }
        val rest = arg.effects - returning

        val evaluatedByFunctor = invokeWithReturningEffects(returning)

        return rest + evaluatedByFunctor
    }

    protected abstract fun invokeWithReturningEffects(list: List<ConditionalEffect>): List<ConditionalEffect>
}
