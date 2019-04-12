/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.AsmUtil.genIncrement
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class Increment(private val myDelta: Int) : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod, isSuper: Boolean, resolvedCall: ResolvedCall<*>): Callable =
            createIntrinsicCallable(method) {
                val jetExpression = resolvedCall.call.calleeExpression
                assert(jetExpression !is KtPrefixExpression) { "There should be postfix increment ${jetExpression!!.text}" }
                genIncrement(returnType, myDelta, it)
            }
}
