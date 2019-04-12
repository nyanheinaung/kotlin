/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.codegen.BlockInfo
import org.jetbrains.kotlin.codegen.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class Concat : IntrinsicMethod() {
    override fun toCallable(
        expression: IrMemberAccessExpression,
        signature: JvmMethodSignature,
        context: JvmBackendContext
    ): IrIntrinsicFunction {
        val argsTypes = expression.receiverAndArgs().asmTypes(context).toMutableList()
        argsTypes[0] = AsmTypes.JAVA_STRING_TYPE

        return object : IrIntrinsicFunction(expression, signature, context, argsTypes) {

            override fun genInvokeInstruction(v: InstructionAdapter) {
                AsmUtil.genInvokeAppendMethod(v, argsTypes[1], null)
                v.invokevirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
            }

            override fun invoke(
                v: InstructionAdapter,
                codegen: org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen,
                data: BlockInfo
            ): StackValue {
                v.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
                v.dup()

                return super.invoke(v, codegen, data)
            }


            override fun genArg(
                expression: IrExpression,
                codegen: org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen,
                index: Int,
                data: BlockInfo
            ) {
                super.genArg(expression, codegen, index, data)
                if (index == 0) {
                    codegen.mv.invokespecial("java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false)
                }
            }
        }
    }
}
