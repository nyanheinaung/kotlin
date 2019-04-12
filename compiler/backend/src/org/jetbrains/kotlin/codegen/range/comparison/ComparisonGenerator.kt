/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.comparison

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.range.getRangeOrProgressionElementType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

interface ComparisonGenerator {
    val comparedType: Type

    fun jumpIfGreaterOrEqual(v: InstructionAdapter, label: Label)
    fun jumpIfLessOrEqual(v: InstructionAdapter, label: Label)
    fun jumpIfGreater(v: InstructionAdapter, label: Label)
    fun jumpIfLess(v: InstructionAdapter, label: Label)
}

interface SignedIntegerComparisonGenerator : ComparisonGenerator {
    fun jumpIfLessThanZero(v: InstructionAdapter, label: Label)
}

fun getComparisonGeneratorForKotlinType(kotlinType: KotlinType): ComparisonGenerator =
    when {
        KotlinBuiltIns.isChar(kotlinType) ->
            CharComparisonGenerator
        KotlinBuiltIns.isByte(kotlinType) || KotlinBuiltIns.isShort(kotlinType) || KotlinBuiltIns.isInt(kotlinType) ->
            IntComparisonGenerator
        KotlinBuiltIns.isLong(kotlinType) ->
            LongComparisonGenerator
        KotlinBuiltIns.isFloat(kotlinType) ->
            FloatComparisonGenerator
        KotlinBuiltIns.isDouble(kotlinType) ->
            DoubleComparisonGenerator
        KotlinBuiltIns.isUInt(kotlinType) ->
            UIntComparisonGenerator
        KotlinBuiltIns.isULong(kotlinType) ->
            ULongComparisonGenerator
        else ->
            throw UnsupportedOperationException("Unexpected element type: $kotlinType")
    }

fun getComparisonGeneratorForRangeContainsCall(
    codegen: ExpressionCodegen,
    call: ResolvedCall<out CallableDescriptor>
): ComparisonGenerator? {
    val descriptor = call.resultingDescriptor

    val receiverType = descriptor.extensionReceiverParameter?.type ?: descriptor.dispatchReceiverParameter?.type ?: return null

    val elementType = getRangeOrProgressionElementType(receiverType) ?: return null

    val valueParameterType = descriptor.valueParameters.singleOrNull()?.type ?: return null

    val asmElementType = codegen.asmType(elementType)
    val asmValueParameterType = codegen.asmType(valueParameterType)

    return when {
        asmElementType == asmValueParameterType ->
            getComparisonGeneratorForKotlinType(elementType)

        asmElementType.isPrimitiveIntOrCoercible() && asmValueParameterType.isPrimitiveIntOrCoercible() ->
            IntComparisonGenerator

        asmElementType.isPrimitiveIntOrCoercible() && asmValueParameterType == Type.LONG_TYPE ||
                asmValueParameterType.isPrimitiveIntOrCoercible() && asmElementType == Type.LONG_TYPE ->
            LongComparisonGenerator

        asmElementType == Type.FLOAT_TYPE && asmValueParameterType == Type.DOUBLE_TYPE ||
                asmElementType == Type.DOUBLE_TYPE && asmValueParameterType == Type.FLOAT_TYPE ->
            DoubleComparisonGenerator

        else -> null
    }
}

private fun Type.isPrimitiveIntOrCoercible() =
    this == Type.INT_TYPE || this == Type.SHORT_TYPE || this == Type.BYTE_TYPE