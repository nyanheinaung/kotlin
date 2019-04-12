/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class Not : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable =
            object : IntrinsicCallable(method) {
                override fun invokeMethodWithArguments(
                        resolvedCall: ResolvedCall<*>,
                        receiver: StackValue,
                        codegen: ExpressionCodegen
                ): StackValue {
                    val element = resolvedCall.call.callElement
                    val stackValue =
                            if (element is KtPrefixExpression) {
                                codegen.gen(element.baseExpression)
                            }
                            else {
                                StackValue.receiver(resolvedCall, receiver, codegen, this)
                            }
                    return StackValue.not(stackValue)
                }
            }
}
