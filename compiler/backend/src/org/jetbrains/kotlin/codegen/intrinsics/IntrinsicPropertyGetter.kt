/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type

abstract class IntrinsicPropertyGetter : IntrinsicMethod() {
    abstract fun generate(
            resolvedCall: ResolvedCall<*>?,
            codegen: ExpressionCodegen,
            returnType: Type,
            receiver: StackValue
    ): StackValue?
}
