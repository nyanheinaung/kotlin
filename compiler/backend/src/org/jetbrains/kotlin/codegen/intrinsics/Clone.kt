/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.resolve.jvm.AsmTypes.OBJECT_TYPE
import org.jetbrains.org.objectweb.asm.Opcodes

class Clone : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod, isSuperCall: Boolean): Callable =
            createUnaryIntrinsicCallable(method, OBJECT_TYPE) {
                val opcode = if (isSuperCall) Opcodes.INVOKESPECIAL else Opcodes.INVOKEVIRTUAL
                it.visitMethodInsn(opcode, "java/lang/Object", "clone", "()Ljava/lang/Object;", false)
            }
}
