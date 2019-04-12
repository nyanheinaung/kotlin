/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil.comparisonOperandType
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class CompareTo : IntrinsicMethod() {
    private fun genInvoke(type: Type?, v: InstructionAdapter) {
        when (type) {
            Type.INT_TYPE, Type.CHAR_TYPE -> v.invokestatic(IntrinsicMethods.INTRINSICS_CLASS_NAME, "compare", "(II)I", false)
            Type.LONG_TYPE -> v.lcmp()
            Type.FLOAT_TYPE -> v.invokestatic("java/lang/Float", "compare", "(FF)I", false)
            Type.DOUBLE_TYPE -> v.invokestatic("java/lang/Double", "compare", "(DD)I", false)
            else -> throw UnsupportedOperationException()
        }
    }

    override fun toCallable(method: CallableMethod): Callable {
        val parameterType = comparisonOperandType(
                method.dispatchReceiverType ?: method.extensionReceiverType,
                method.parameterTypes.single()
        )
        return createBinaryIntrinsicCallable(method.returnType, parameterType, parameterType, null) {
            genInvoke(parameterType, it)
        }
    }
}
