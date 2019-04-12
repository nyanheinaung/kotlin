/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil.numberFunctionOperandType
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.org.objectweb.asm.Type

class Inv : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable {
        val intermediateResultType = numberFunctionOperandType(method.returnType)
        return createUnaryIntrinsicCallable(method) {
            if (returnType == Type.LONG_TYPE) {
                it.lconst(-1)
            }
            else {
                it.iconst(-1)
            }
            it.xor(intermediateResultType)
            StackValue.coerce(intermediateResultType, returnType, it)
        }
    }
}
