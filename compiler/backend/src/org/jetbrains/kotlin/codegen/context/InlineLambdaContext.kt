/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context

import org.jetbrains.kotlin.codegen.OwnerKind
import org.jetbrains.kotlin.codegen.binding.MutableClosure
import org.jetbrains.kotlin.descriptors.FunctionDescriptor

class InlineLambdaContext(
        functionDescriptor: FunctionDescriptor,
        contextKind: OwnerKind,
        parentContext: CodegenContext<*>,
        closure: MutableClosure?,
        val isCrossInline: Boolean,
        private val isPropertyReference: Boolean
) : MethodContext(functionDescriptor, contextKind, parentContext, closure, false) {

    override fun getFirstCrossInlineOrNonInlineContext(): CodegenContext<*> {
        if (isCrossInline) return this

        val parent = if (isPropertyReference) parentContext as? AnonymousClassContext else  { parentContext as? ClosureContext } ?:
                     throw AssertionError(
                             "Parent of inlining lambda body should be " +
                             "${if (isPropertyReference) "ClosureContext" else "AnonymousClassContext"}, but: $parentContext"
                     )

        val grandParent = parent.parentContext ?:
                          throw AssertionError("Parent context of lambda class context should exist: $contextDescriptor")
        return grandParent.firstCrossInlineOrNonInlineContext
    }

}