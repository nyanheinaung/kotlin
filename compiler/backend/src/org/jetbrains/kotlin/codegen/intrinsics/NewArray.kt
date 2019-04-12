/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class NewArray : IntrinsicMethod() {
    override fun toCallable(fd: FunctionDescriptor, isSuper: Boolean, resolvedCall: ResolvedCall<*>, codegen: ExpressionCodegen): Callable {
        val jetType = resolvedCall.resultingDescriptor.returnType!!
        val type = codegen.state.typeMapper.mapType(jetType)
        return object : IntrinsicCallable(type, listOf(Type.INT_TYPE), null, null) {
            override fun invokeIntrinsic(v: InstructionAdapter) {
                codegen.newArrayInstruction(jetType)
            }
        }
    }
}
