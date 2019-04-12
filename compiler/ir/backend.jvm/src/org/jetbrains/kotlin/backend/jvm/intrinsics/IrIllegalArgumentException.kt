/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.codegen.BlockInfo
import org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.resolve.jvm.AsmTypes.JAVA_STRING_TYPE
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class IrIllegalArgumentException : IntrinsicMethod() {
    val exceptionTypeDescriptor = Type.getType(IllegalArgumentException::class.java)!!

    override fun toCallable(
        expression: IrMemberAccessExpression,
        signature: JvmMethodSignature,
        context: JvmBackendContext
    ): IrIntrinsicFunction {
        return object : IrIntrinsicFunction(expression, signature, context, listOf(JAVA_STRING_TYPE)) {
            override fun genInvokeInstruction(v: InstructionAdapter) {
                v.invokespecial(
                    exceptionTypeDescriptor.internalName,
                    "<init>",
                    Type.getMethodDescriptor(Type.VOID_TYPE, JAVA_STRING_TYPE),
                    false
                )
                v.athrow()
            }

            override fun invoke(v: InstructionAdapter, codegen: ExpressionCodegen, data: BlockInfo): StackValue {
                v.anew(exceptionTypeDescriptor)
                v.dup()
                return super.invoke(v, codegen, data)
            }
        }
    }
}