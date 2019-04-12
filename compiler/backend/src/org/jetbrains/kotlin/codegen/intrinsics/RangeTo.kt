/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.codegen.FrameMap
import org.jetbrains.kotlin.codegen.generateNewInstanceDupAndPlaceBeforeStackTop
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.Type.*
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class RangeTo : IntrinsicMethod() {
    private fun rangeTypeToPrimitiveType(rangeType: Type): Type {
        val fqName = rangeType.internalName
        val name = fqName.substringAfter("kotlin/ranges/").substringBefore("Range")
        return when (name) {
            "Double" -> DOUBLE_TYPE
            "Float" -> FLOAT_TYPE
            "Long" -> LONG_TYPE
            "Int" -> INT_TYPE
            "Short" -> SHORT_TYPE
            "Char" -> CHAR_TYPE
            "Byte" -> BYTE_TYPE
            else -> throw IllegalStateException("RangeTo intrinsic can only work for primitive types: $fqName")
        }
    }

    override fun toCallable(method: CallableMethod): Callable {
        val argType = rangeTypeToPrimitiveType(method.returnType)
        return object : IntrinsicCallable(
                method.returnType,
                method.valueParameterTypes.map { argType },
                nullOr(method.dispatchReceiverType, argType),
                nullOr(method.extensionReceiverType, argType)
        ) {
            override fun afterReceiverGeneration(v: InstructionAdapter, frameMap: FrameMap) {
                v.generateNewInstanceDupAndPlaceBeforeStackTop(frameMap, argType, returnType.internalName)
            }

            override fun invokeIntrinsic(v: InstructionAdapter) {
                v.invokespecial(returnType.internalName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, argType, argType), false)
            }
        }
    }
}
