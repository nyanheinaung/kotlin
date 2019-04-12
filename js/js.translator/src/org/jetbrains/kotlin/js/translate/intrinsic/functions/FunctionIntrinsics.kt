/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.intrinsic.functions

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.js.translate.context.TranslationContext
import org.jetbrains.kotlin.js.translate.intrinsic.functions.basic.FunctionIntrinsic
import org.jetbrains.kotlin.js.translate.intrinsic.functions.factories.*
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult

class FunctionIntrinsics {

    private val intrinsicCache = mutableMapOf<FunctionDescriptor, FunctionIntrinsic?>()

    private val factories = listOf(
        LongOperationFIF,
        PrimitiveUnaryOperationFIF.INSTANCE,
        StringPlusCharFIF,
        PrimitiveBinaryOperationFIF.INSTANCE,
        ArrayFIF,
        TopLevelFIF.INSTANCE,
        NumberAndCharConversionFIF,
        ThrowableConstructorIntrinsicFactory,
        ExceptionPropertyIntrinsicFactory,
        AsDynamicFIF,
        CoroutineContextFIF,
        SuspendCoroutineUninterceptedOrReturnFIF,
        InterceptedFIF
    )

    fun getIntrinsic(descriptor: FunctionDescriptor, context: TranslationContext): FunctionIntrinsic? {
        if (descriptor in intrinsicCache) return intrinsicCache[descriptor]

        return factories.firstNotNullResult { it.getIntrinsic(descriptor, context) }.also {
            intrinsicCache[descriptor] = it
        }
    }
}
