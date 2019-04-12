/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.codegen.BlockInfo
import org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.OwnerKind
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class IrEnumValueOf : IntrinsicMethod() {
    override fun toCallable(expression: IrMemberAccessExpression, signature: JvmMethodSignature, context: JvmBackendContext): IrIntrinsicFunction {
        val enumType = context.state.typeMapper.mapType(expression.descriptor.returnType!!)
        val newSignature = context.state.typeMapper.mapSignatureSkipGeneric(expression.descriptor as FunctionDescriptor, OwnerKind.IMPLEMENTATION)
        val stringType = AsmTypes.JAVA_STRING_TYPE;

        return object : IrIntrinsicFunction(expression, newSignature, context, listOf(stringType)) {
            override fun invoke(v: InstructionAdapter, codegen: ExpressionCodegen, data: BlockInfo): StackValue {
                v.tconst(enumType)
                codegen.gen(expression.getValueArgument(0)!!, stringType, data)
                v.invokestatic("java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
                v.checkcast(enumType)
                return StackValue.onStack(enumType)
            }
        }
    }
}