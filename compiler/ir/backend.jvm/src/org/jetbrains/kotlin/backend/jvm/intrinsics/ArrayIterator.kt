/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature

class ArrayIterator : IntrinsicMethod() {
    override fun toCallable(expression: IrMemberAccessExpression, signature: JvmMethodSignature, context: JvmBackendContext): IrIntrinsicFunction {
        val method = context.state.typeMapper.mapToCallableMethod(expression.descriptor as FunctionDescriptor, false)
        return IrIntrinsicFunction.create(expression, signature, context, method.owner) {
            val methodSignature = "(${method.owner.descriptor})${method.returnType.descriptor}"
            val intrinsicOwner =
                    if (AsmUtil.isPrimitive(method.owner.elementType))
                        "kotlin/jvm/internal/ArrayIteratorsKt"
                    else
                        "kotlin/jvm/internal/ArrayIteratorKt"
            it.invokestatic(intrinsicOwner, "iterator", methodSignature, false)
        }
    }
}
