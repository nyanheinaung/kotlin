/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.calls.smartcasts.getReceiverValueWithSmartCast
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.synthetic.SamAdapterExtensionFunctionDescriptor
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor

object ProtectedSyntheticExtensionCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val descriptor = resolvedCall.resultingDescriptor

        val sourceFunction = when (descriptor) {
            is SyntheticJavaPropertyDescriptor -> descriptor.getMethod
            // TODO: this branch becomes unnecessary, because common checks are applied to SAM adapters being resolved as common members
            // But this part may be still useful when we enable backward compatibility mode and SAM adapters become extensions again
            is SamAdapterExtensionFunctionDescriptor -> descriptor.baseDescriptorForSynthetic
            else -> return
        }

        val from = context.scope.ownerDescriptor

        // Already reported
        if (!Visibilities.isVisibleIgnoringReceiver(descriptor, from)) return

        if (resolvedCall.dispatchReceiver != null && resolvedCall.extensionReceiver !is ReceiverValue) return

        val receiverValue = resolvedCall.extensionReceiver as ReceiverValue
        val receiverTypes = listOf(receiverValue.type) + context.dataFlowInfo.getStableTypes(
                context.dataFlowValueFactory.createDataFlowValue(receiverValue, context.trace.bindingContext, context.scope.ownerDescriptor),
                context.languageVersionSettings
        )

        if (receiverTypes.none { Visibilities.isVisible(getReceiverValueWithSmartCast(null, it), sourceFunction, from) }) {
            context.trace.report(Errors.INVISIBLE_MEMBER.on(reportOn, descriptor, descriptor.visibility, from))
        }
    }
}
