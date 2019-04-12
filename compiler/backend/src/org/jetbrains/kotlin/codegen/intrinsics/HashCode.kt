/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class HashCode(private val jvmTarget: JvmTarget) : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable {
        val receiverType = method.dispatchReceiverType ?: method.extensionReceiverType ?: error("No receiver for callable: $method")
        val useObjectHashCode = JvmTarget.JVM_1_6 == jvmTarget || !AsmUtil.isPrimitive(receiverType)
        return object : IntrinsicCallable(
                Type.INT_TYPE,
                emptyList(),
                if (useObjectHashCode) nullOrObject(method.dispatchReceiverType) else method.dispatchReceiverType,
                if (useObjectHashCode) nullOrObject(method.extensionReceiverType) else method.extensionReceiverType
        ) {
            override fun invokeIntrinsic(v: InstructionAdapter) {
                v.invokeHashCode(if (useObjectHashCode) AsmTypes.OBJECT_TYPE else receiverType)
            }
        }
    }

    companion object {
        fun InstructionAdapter.invokeHashCode(type: Type) {
            if (AsmUtil.isPrimitive(type)) {
                val boxedType = AsmUtil.boxType(type)
                visitMethodInsn(Opcodes.INVOKESTATIC, boxedType.internalName, "hashCode", Type.getMethodDescriptor(Type.INT_TYPE, type), false)
            }
            else {
                visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false)
            }
        }
    }
}
