/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.codegen

import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.ValueKind
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.org.objectweb.asm.Type

interface IrCallGenerator {

    fun genCall(callableMethod: Callable, callDefault: Boolean, codegen: ExpressionCodegen, expression: IrMemberAccessExpression) {
        if (!callDefault) {
            callableMethod.genInvokeInstruction(codegen.mv)
        } else {
            (callableMethod as CallableMethod).genInvokeDefaultInstruction(codegen.mv)
        }
    }

    fun beforeValueParametersStart() {

    }

    fun genValueAndPut(
        valueParameterDescriptor: ValueParameterDescriptor?,
        argumentExpression: IrExpression,
        parameterType: Type,
        parameterIndex: Int,
        codegen: ExpressionCodegen,
        blockInfo: BlockInfo
    ) {
        codegen.gen(argumentExpression, parameterType, blockInfo)
    }

    fun putValueIfNeeded(parameterType: Type, value: StackValue, kind: ValueKind, parameterIndex: Int, codegen: ExpressionCodegen) {
        value.put(parameterType, codegen.visitor)
    }

    object DefaultCallGenerator : IrCallGenerator
}