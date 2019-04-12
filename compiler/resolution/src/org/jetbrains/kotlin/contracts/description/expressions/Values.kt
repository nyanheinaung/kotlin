/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.description.expressions

import org.jetbrains.kotlin.contracts.description.BooleanExpression
import org.jetbrains.kotlin.contracts.description.ContractDescriptionElement
import org.jetbrains.kotlin.contracts.description.ContractDescriptionVisitor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor


interface ContractDescriptionValue : ContractDescriptionElement {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitValue(this, data)
}

open class ConstantReference(val name: String) : ContractDescriptionValue {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitConstantDescriptor(this, data)

    companion object {
        val NULL = ConstantReference("NULL")
        val WILDCARD = ConstantReference("WILDCARD")
        val NOT_NULL = ConstantReference("NOT_NULL")
    }
}

class BooleanConstantReference(name: String) : ConstantReference(name), BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitBooleanConstantDescriptor(this, data)

    companion object {
        val TRUE = BooleanConstantReference("TRUE")
        val FALSE = BooleanConstantReference("FALSE")
    }
}

open class VariableReference(val descriptor: ParameterDescriptor) : ContractDescriptionValue {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D) =
        contractDescriptionVisitor.visitVariableReference(this, data)
}

class BooleanVariableReference(descriptor: ParameterDescriptor) : VariableReference(descriptor), BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitBooleanVariableReference(this, data)
}