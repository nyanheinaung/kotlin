/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod

class ArrayIterator : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable =
            createUnaryIntrinsicCallable(method) {
                val methodSignature = "(${method.owner.descriptor})${returnType.descriptor}"
                val intrinsicOwner =
                        if (AsmUtil.isPrimitive(method.owner.elementType))
                            "kotlin/jvm/internal/ArrayIteratorsKt"
                        else
                            "kotlin/jvm/internal/ArrayIteratorKt"
                it.invokestatic(intrinsicOwner, "iterator", methodSignature, false)
            }
}
