/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.resolve.jvm.AsmTypes.OBJECT_TYPE
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class MonitorInstruction private constructor(private val opcode: Int) : IntrinsicMethod() {
    companion object {
        @JvmField
        val MONITOR_ENTER: MonitorInstruction = MonitorInstruction(Opcodes.MONITORENTER)

        @JvmField
        val MONITOR_EXIT: MonitorInstruction = MonitorInstruction(Opcodes.MONITOREXIT)
    }

    override fun toCallable(method: CallableMethod): Callable {
        return object : IntrinsicCallable(Type.VOID_TYPE, listOf(OBJECT_TYPE), null, null) {
            override fun invokeIntrinsic(v: InstructionAdapter) {
                v.visitInsn(opcode)
            }
        }
    }
}
