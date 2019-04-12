/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.resolve.jvm.AsmTypes.OBJECT_TYPE
import org.jetbrains.org.objectweb.asm.Type

internal val equalsMethodDescriptor: String =
        Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(Any::class.java));

class Equals : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable =
            createBinaryIntrinsicCallable(
                    method.returnType,
                    OBJECT_TYPE,
                    nullOrObject(method.dispatchReceiverType),
                    nullOrObject(method.extensionReceiverType)
            ) {
                AsmUtil.genAreEqualCall(it)
            }
}

class EqualsThrowingNpeForNullReceiver(private val lhsType: Type) : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable =
            createBinaryIntrinsicCallable(
                    method.returnType,
                    OBJECT_TYPE,
                    nullOrObject(method.dispatchReceiverType),
                    nullOrObject(method.extensionReceiverType)
            ) {
                it.invokevirtual(lhsType.internalName, "equals", equalsMethodDescriptor, false)
            }
}
