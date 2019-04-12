/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.codegen.AsmUtil.numberFunctionOperandType
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.org.objectweb.asm.Type

class Inv : IntrinsicMethod() {
    /*TODO new this type*/
    override fun toCallable(expression: IrMemberAccessExpression, signature: JvmMethodSignature, context: JvmBackendContext): IrIntrinsicFunction {
        val returnType = signature.returnType
        val type = numberFunctionOperandType(returnType)
        return IrIntrinsicFunction.create(expression, signature, context, type) {
            if (returnType == Type.LONG_TYPE) {
                it.lconst(-1)
            }
            else {
                it.iconst(-1)
            }
            it.xor(returnType)
        }
    }
}
