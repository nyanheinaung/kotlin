/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.interpretation

import org.jetbrains.kotlin.contracts.description.ContractDescriptionVisitor
import org.jetbrains.kotlin.contracts.description.expressions.*
import org.jetbrains.kotlin.contracts.model.ESExpression
import org.jetbrains.kotlin.contracts.model.functors.IsFunctor
import org.jetbrains.kotlin.contracts.model.structure.*

internal class ConditionInterpreter(
    private val dispatcher: ContractInterpretationDispatcher
) : ContractDescriptionVisitor<ESExpression?, Unit> {
    private val constants = dispatcher.components.constants

    override fun visitLogicalOr(logicalOr: LogicalOr, data: Unit): ESExpression? {
        val left = logicalOr.left.accept(this, data) ?: return null
        val right = logicalOr.right.accept(this, data) ?: return null
        return ESOr(constants, left, right)
    }

    override fun visitLogicalAnd(logicalAnd: LogicalAnd, data: Unit): ESExpression? {
        val left = logicalAnd.left.accept(this, data) ?: return null
        val right = logicalAnd.right.accept(this, data) ?: return null
        return ESAnd(constants, left, right)
    }

    override fun visitLogicalNot(logicalNot: LogicalNot, data: Unit): ESExpression? {
        val arg = logicalNot.arg.accept(this, data) ?: return null
        return ESNot(constants, arg)
    }

    override fun visitIsInstancePredicate(isInstancePredicate: IsInstancePredicate, data: Unit): ESExpression? {
        val esVariable = dispatcher.interpretVariable(isInstancePredicate.arg) ?: return null
        return ESIs(esVariable, IsFunctor(constants, isInstancePredicate.type, isInstancePredicate.isNegated))
    }

    override fun visitIsNullPredicate(isNullPredicate: IsNullPredicate, data: Unit): ESExpression? {
        val variable = dispatcher.interpretVariable(isNullPredicate.arg) ?: return null
        return ESEqual(constants, variable, constants.nullValue, isNullPredicate.isNegated)
    }

    override fun visitBooleanConstantDescriptor(booleanConstantDescriptor: BooleanConstantReference, data: Unit): ESExpression? =
        dispatcher.interpretConstant(booleanConstantDescriptor)

    override fun visitBooleanVariableReference(booleanVariableReference: BooleanVariableReference, data: Unit): ESExpression? =
        dispatcher.interpretVariable(booleanVariableReference)
}
