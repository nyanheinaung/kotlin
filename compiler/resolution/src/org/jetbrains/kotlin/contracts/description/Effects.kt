/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.description

import org.jetbrains.kotlin.contracts.description.expressions.ConstantReference
import org.jetbrains.kotlin.contracts.description.expressions.VariableReference

/**
 * Effect with condition attached to it.
 *
 * [condition] is some expression, which result-type is Boolean, and clause should
 * be interpreted as: "if [effect] took place then [condition]-expression is
 * guaranteed to be true"
 *
 * NB. [effect] and [condition] connected with implication in math logic sense:
 * [effect] => [condition]. In particular this means that:
 *  - there can be multiple ways how [effect] can be produced, but for any of them
 *    [condition] holds.
 *  - if [effect] wasn't observed, we *can't* reason that [condition] is false
 *  - if [condition] is true, we *can't* reason that [effect] will be observed.
 */
class ConditionalEffectDeclaration(val effect: EffectDeclaration, val condition: BooleanExpression) : EffectDeclaration {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitConditionalEffectDeclaration(this, data)
}


/**
 * Effect which specifies that subroutine returns some particular value
 */
class ReturnsEffectDeclaration(val value: ConstantReference) : EffectDeclaration {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitReturnsEffectDeclaration(this, data)

}


/**
 * Effect which specifies, that during execution of subroutine, callable [variableReference] will be invoked
 * [kind] amount of times, and will never be invoked after subroutine call is finished.
 */
class CallsEffectDeclaration(val variableReference: VariableReference, val kind: InvocationKind) : EffectDeclaration {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitCallsEffectDeclaration(this, data)
}