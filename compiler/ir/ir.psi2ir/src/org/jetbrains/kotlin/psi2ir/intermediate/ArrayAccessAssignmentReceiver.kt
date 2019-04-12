/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockImpl
import org.jetbrains.kotlin.ir.expressions.impl.inlineStatement
import org.jetbrains.kotlin.ir.expressions.isAssignmentOperatorWithResult
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi2ir.generators.CallGenerator
import org.jetbrains.kotlin.psi2ir.generators.generateSamConversionForValueArgumentsIfRequired
import org.jetbrains.kotlin.psi2ir.generators.pregenerateValueArgumentsUsing
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.tasks.isDynamic
import org.jetbrains.kotlin.types.KotlinType

class ArrayAccessAssignmentReceiver(
    private val irArray: IrExpression,
    private val ktIndexExpressions: List<KtExpression>,
    private val irIndexExpressions: List<IrExpression>,
    private val indexedGetResolvedCall: ResolvedCall<FunctionDescriptor>?,
    private val indexedSetResolvedCall: ResolvedCall<FunctionDescriptor>?,
    private val indexedGetCall: () -> CallBuilder?,
    private val indexedSetCall: () -> CallBuilder?,
    private val callGenerator: CallGenerator,
    private val startOffset: Int,
    private val endOffset: Int,
    private val origin: IrStatementOrigin
) : AssignmentReceiver {

    private val indexedGetDescriptor = indexedGetResolvedCall?.resultingDescriptor
    private val indexedSetDescriptor = indexedSetResolvedCall?.resultingDescriptor

    private val descriptor =
        indexedGetDescriptor
            ?: indexedSetDescriptor
            ?: throw AssertionError("Array access should have either indexed-get call or indexed-set call")

    override fun assign(withLValue: (LValue) -> IrExpression): IrExpression {
        val kotlinType: KotlinType =
            indexedGetDescriptor?.returnType
                ?: indexedSetDescriptor?.run { valueParameters.last().type }
                ?: throw AssertionError("Array access should have either indexed-get call or indexed-set call")

        val hasResult = origin.isAssignmentOperatorWithResult()
        val resultType = if (hasResult) kotlinType else callGenerator.context.builtIns.unitType
        val irResultType = callGenerator.translateType(resultType)

        if (indexedGetDescriptor?.isDynamic() != false && indexedSetDescriptor?.isDynamic() != false) {
            return withLValue(
                createLValue(kotlinType, OnceExpressionValue(irArray)) { _, irIndex ->
                    OnceExpressionValue(irIndex)
                }
            )
        }

        val irBlock = IrBlockImpl(startOffset, endOffset, irResultType, origin)

        val irArrayValue = callGenerator.scope.createTemporaryVariableInBlock(callGenerator.context, irArray, irBlock, "array")

        irBlock.inlineStatement(
            withLValue(
                createLValue(kotlinType, irArrayValue) { i, irIndex ->
                    callGenerator.scope.createTemporaryVariableInBlock(callGenerator.context, irIndex, irBlock, "index$i")
                }
            )
        )

        return irBlock
    }

    private fun createLValue(
        kotlinType: KotlinType,
        irArrayValue: IntermediateValue,
        createIndexValue: (Int, IrExpression) -> IntermediateValue
    ): LValueWithGetterAndSetterCalls {
        val ktExpressionToIrIndexValue = HashMap<KtExpression, IntermediateValue>()
        for ((i, irIndex) in irIndexExpressions.withIndex()) {
            ktExpressionToIrIndexValue[ktIndexExpressions[i]] =
                createIndexValue(i, irIndex)
        }

        return LValueWithGetterAndSetterCalls(
            callGenerator,
            descriptor,
            { indexedGetCall()?.fillArguments(irArrayValue, indexedGetResolvedCall!!, ktExpressionToIrIndexValue, null) },
            { indexedSetCall()?.fillArguments(irArrayValue, indexedSetResolvedCall!!, ktExpressionToIrIndexValue, it) },
            callGenerator.translateType(kotlinType),
            startOffset, endOffset, origin
        )
    }

    override fun assign(value: IrExpression): IrExpression {
        val call = indexedSetCall() ?: throw AssertionError("Array access without indexed-get call")
        val ktExpressionToIrIndexExpression = ktIndexExpressions.zip(irIndexExpressions.map { OnceExpressionValue(it) }).toMap()
        call.fillArguments(OnceExpressionValue(irArray), indexedSetResolvedCall!!, ktExpressionToIrIndexExpression, value)
        return callGenerator.generateCall(startOffset, endOffset, call, IrStatementOrigin.EQ)
    }

    private fun CallBuilder.fillArguments(
        arrayValue: IntermediateValue,
        resolvedCall: ResolvedCall<FunctionDescriptor>,
        ktExpressionToIrIndexValue: Map<KtExpression, IntermediateValue>,
        value: IrExpression?
    ) = apply {
        setExplicitReceiverValue(arrayValue)
        callGenerator.statementGenerator.pregenerateValueArgumentsUsing(this, resolvedCall) { ktExpression ->
            ktExpressionToIrIndexValue[ktExpression]?.load()
        }
        value?.let { lastArgument = it }
        callGenerator.statementGenerator.generateSamConversionForValueArgumentsIfRequired(this, resolvedCall.resultingDescriptor)
    }
}
