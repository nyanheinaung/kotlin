/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil.numberFunctionOperandType
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod

class UnaryMinus : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable =
            createUnaryIntrinsicCallable(method, numberFunctionOperandType(method.returnType), needPrimitiveCheck = true) {
                it.neg(returnType)
            }
}
