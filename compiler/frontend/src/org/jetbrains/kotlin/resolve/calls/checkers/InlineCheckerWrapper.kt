/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import java.lang.ref.WeakReference

class InlineCheckerWrapper : CallChecker {
    private var checkersCache: WeakReference<MutableMap<DeclarationDescriptor, CallChecker>>? = null

    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (context.isAnnotationContext) return

        var parentDescriptor: DeclarationDescriptor? = context.scope.ownerDescriptor

        while (parentDescriptor != null) {
            if (InlineUtil.isInline(parentDescriptor)) {
                val checker = getChecker(parentDescriptor as FunctionDescriptor)
                checker.check(resolvedCall, reportOn, context)
            }

            parentDescriptor = parentDescriptor.containingDeclaration
        }
    }

    private fun getChecker(descriptor: FunctionDescriptor): CallChecker {
        val map = checkersCache?.get() ?: hashMapOf()
        checkersCache = checkersCache ?: WeakReference(map)
        return map.getOrPut(descriptor) { InlineChecker(descriptor) }
    }
}
