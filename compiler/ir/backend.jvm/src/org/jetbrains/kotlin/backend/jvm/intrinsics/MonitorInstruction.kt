/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.resolve.jvm.AsmTypes.OBJECT_TYPE
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.org.objectweb.asm.Opcodes

class MonitorInstruction private constructor(private val opcode: Int) : IntrinsicMethod() {
    companion object {
        @JvmField
        val MONITOR_ENTER: MonitorInstruction = MonitorInstruction(Opcodes.MONITORENTER)

        @JvmField
        val MONITOR_EXIT: MonitorInstruction = MonitorInstruction(Opcodes.MONITOREXIT)
    }

    /*TODO void return type*/
    override fun toCallable(expression: IrMemberAccessExpression, signature: JvmMethodSignature, context: JvmBackendContext): IrIntrinsicFunction {
        return IrIntrinsicFunction.create(expression, signature, context, OBJECT_TYPE) {
            it.visitInsn(opcode)
        }
    }

}
