/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCallWithAssert
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type

object LateinitIsInitialized : IntrinsicPropertyGetter() {
    override fun generate(resolvedCall: ResolvedCall<*>?, codegen: ExpressionCodegen, returnType: Type, receiver: StackValue): StackValue? {
        val value = getStackValue(resolvedCall ?: return null, codegen) ?: return null
        return StackValue.compareWithNull(value, Opcodes.IFNULL)
    }
}

private fun getStackValue(resolvedCall: ResolvedCall<*>, codegen: ExpressionCodegen): StackValue? {
    val expression =
            (resolvedCall.extensionReceiver as? ExpressionReceiver)?.expression as? KtCallableReferenceExpression ?: return null
    val referenceResolvedCall = expression.callableReference.getResolvedCallWithAssert(codegen.bindingContext)
    val receiver = codegen.generateCallableReferenceReceiver(referenceResolvedCall) ?: StackValue.none()
    val target = referenceResolvedCall.resultingDescriptor as PropertyDescriptor
    return codegen.intermediateValueForProperty(target, true, false, null, false, receiver, null, true)
}
