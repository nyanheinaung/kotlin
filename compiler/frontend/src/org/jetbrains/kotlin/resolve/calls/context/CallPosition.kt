/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.context

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall


sealed class CallPosition {
    object Unknown : CallPosition()

    class ExtensionReceiverPosition(val resolvedCall: ResolvedCall<*>) : CallPosition()

    class ValueArgumentPosition(
        val resolvedCall: ResolvedCall<*>,
        val valueParameter: ValueParameterDescriptor,
        val valueArgument: ValueArgument
    ) : CallPosition()

    class PropertyAssignment(val leftPart: KtExpression?) : CallPosition()
}
