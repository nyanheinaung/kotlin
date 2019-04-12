/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.visitors

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.contracts.model.Computation
import org.jetbrains.kotlin.contracts.model.ESExpression
import org.jetbrains.kotlin.contracts.model.ESExpressionVisitor
import org.jetbrains.kotlin.contracts.model.structure.*

/**
 * Given an [ESExpression], substitutes all variables in it using provided [substitutions] map,
 * and then flattens resulting tree, producing an [EffectSchema], which describes effects
 * of this [ESExpression] with effects of arguments taken into consideration.
 */
class Substitutor(
    private val substitutions: Map<ESVariable, Computation>,
    private val builtIns: KotlinBuiltIns
) : ESExpressionVisitor<Computation?> {
    override fun visitIs(isOperator: ESIs): Computation? {
        val arg = isOperator.left.accept(this) ?: return null
        return CallComputation(builtIns.booleanType, isOperator.functor.invokeWithArguments(arg))
    }

    override fun visitNot(not: ESNot): Computation? {
        val arg = not.arg.accept(this) ?: return null
        return CallComputation(builtIns.booleanType, not.functor.invokeWithArguments(arg))
    }

    override fun visitEqual(equal: ESEqual): Computation? {
        val left = equal.left.accept(this) ?: return null
        val right = equal.right.accept(this) ?: return null
        return CallComputation(builtIns.booleanType, equal.functor.invokeWithArguments(listOf(left, right)))
    }

    override fun visitAnd(and: ESAnd): Computation? {
        val left = and.left.accept(this) ?: return null
        val right = and.right.accept(this) ?: return null
        return CallComputation(builtIns.booleanType, and.functor.invokeWithArguments(left, right))
    }

    override fun visitOr(or: ESOr): Computation? {
        val left = or.left.accept(this) ?: return null
        val right = or.right.accept(this) ?: return null
        return CallComputation(builtIns.booleanType, or.functor.invokeWithArguments(left, right))
    }

    override fun visitVariable(esVariable: ESVariable): Computation? = substitutions[esVariable] ?: esVariable

    override fun visitConstant(esConstant: ESConstant): Computation? = esConstant

    override fun visitReceiver(esReceiver: ESReceiver): ESReceiver = esReceiver
}
