/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil.correctElementType
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod

class ArrayGet : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable =
            createIntrinsicCallable(method) {
                val type = correctElementType(calcReceiverType())
                it.aload(type)
            }
}
