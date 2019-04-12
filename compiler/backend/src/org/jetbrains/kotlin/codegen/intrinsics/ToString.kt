/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod

class ToString : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable {
        val type = AsmUtil.stringValueOfType(method.dispatchReceiverType ?: method.extensionReceiverType)
        return createUnaryIntrinsicCallable(method, newThisType = type) {
            it.invokestatic("java/lang/String", "valueOf", "(${type.descriptor})Ljava/lang/String;", false)
        }
    }
}
