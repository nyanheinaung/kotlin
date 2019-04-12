/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts

import org.jetbrains.kotlin.contracts.model.ESExpressionVisitor
import org.jetbrains.kotlin.contracts.model.structure.AbstractESValue
import org.jetbrains.kotlin.contracts.model.structure.ESReceiverValue
import org.jetbrains.kotlin.contracts.model.structure.ESVariable
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue


/**
 * [ESDataFlowValue] is an interface, representing some entity that holds [DataFlowValue] for DFA.
 *
 * Actually that interfaces must be sealed.
 */
interface ESDataFlowValue {
    val dataFlowValue: DataFlowValue

    fun dataFlowEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ESDataFlowValue) return false

        return dataFlowValue == other.dataFlowValue
    }
}


/**
 * [ESVariableWithDataFlowValue] is [ESVariable] with data flow information.
 */
class ESVariableWithDataFlowValue(
    descriptor: ValueDescriptor,
    override val dataFlowValue: DataFlowValue
) : ESVariable(descriptor), ESDataFlowValue {
    override fun equals(other: Any?): Boolean = dataFlowEquals(other)

    override fun hashCode(): Int {
        return dataFlowValue.hashCode()
    }
}


/**
 * [ESReceiverWithDataFlowValue] is [ESReceiverValue] with data flow information.
 */
class ESReceiverWithDataFlowValue(
    receiverValue: ReceiverValue,
    override val dataFlowValue: DataFlowValue
) : ESReceiverValue(receiverValue), ESDataFlowValue {
    override fun equals(other: Any?): Boolean = dataFlowEquals(other)

    override fun hashCode(): Int {
        return dataFlowValue.hashCode()
    }
}


/**
 * [ESLambda] represents lambda functions in Effect System
 */
class ESLambda(val lambda: KtLambdaExpression) : AbstractESValue(null) {
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T {
        throw IllegalStateException("Lambdas shouldn't be visited by ESExpressionVisitor")
    }
}