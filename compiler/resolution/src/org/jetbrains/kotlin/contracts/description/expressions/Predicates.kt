/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.description.expressions

import org.jetbrains.kotlin.contracts.description.BooleanExpression
import org.jetbrains.kotlin.contracts.description.ContractDescriptionVisitor
import org.jetbrains.kotlin.types.KotlinType

class IsInstancePredicate(val arg: VariableReference, val type: KotlinType, val isNegated: Boolean) : BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitIsInstancePredicate(this, data)

    fun negated(): IsInstancePredicate = IsInstancePredicate(arg, type, isNegated.not())
}

class IsNullPredicate(val arg: VariableReference, val isNegated: Boolean) : BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitIsNullPredicate(this, data)

    fun negated(): IsNullPredicate = IsNullPredicate(arg, isNegated.not())
}
