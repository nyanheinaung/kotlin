/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.structure

import org.jetbrains.kotlin.contracts.model.ESExpression
import org.jetbrains.kotlin.contracts.model.ESExpressionVisitor
import org.jetbrains.kotlin.contracts.model.ESOperator
import org.jetbrains.kotlin.contracts.model.ESValue
import org.jetbrains.kotlin.contracts.model.functors.*

class ESAnd(val constants: ESConstants, val left: ESExpression, val right: ESExpression) : ESOperator {
    override val functor: AndFunctor = AndFunctor(constants)
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitAnd(this)
}

class ESOr(val constants: ESConstants, val left: ESExpression, val right: ESExpression) : ESOperator {
    override val functor: OrFunctor = OrFunctor(constants)
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitOr(this)
}

class ESNot(val constants: ESConstants, val arg: ESExpression) : ESOperator {
    override val functor = NotFunctor(constants)
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitNot(this)
}

class ESIs(val left: ESValue, override val functor: IsFunctor) : ESOperator {
    val type = functor.type
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitIs(this)
}

class ESEqual(val constants: ESConstants, val left: ESValue, val right: ESValue, isNegated: Boolean) : ESOperator {
    override val functor: EqualsFunctor = EqualsFunctor(constants, isNegated)
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitEqual(this)
}

fun ESExpression.and(other: ESExpression?, constants: ESConstants): ESExpression =
    if (other == null) this else ESAnd(constants, this, other)

fun ESExpression.or(other: ESExpression?, constants: ESConstants): ESExpression =
    if (other == null) this else ESOr(constants, this, other)
