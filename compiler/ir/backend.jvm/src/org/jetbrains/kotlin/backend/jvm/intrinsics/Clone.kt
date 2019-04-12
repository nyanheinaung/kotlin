/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.org.objectweb.asm.Opcodes

class Clone : IntrinsicMethod() {

    override fun toCallable(expression: IrMemberAccessExpression, signature: JvmMethodSignature, context: JvmBackendContext): IrIntrinsicFunction {
        return IrIntrinsicFunction.create(expression, signature.newReturnType(AsmTypes.OBJECT_TYPE), context) {
            val opcode = if (expression is IrCall && expression.superQualifier != null) Opcodes.INVOKESPECIAL else Opcodes.INVOKEVIRTUAL
            it.visitMethodInsn(opcode, "java/lang/Object", "clone", "()Ljava/lang/Object;", false)
        }
    }

}
