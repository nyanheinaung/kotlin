/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.resolve.scopes.receivers.prepareReceiverRegardingCaptureTypes


class FakeKotlinCallArgumentForCallableReference(
    val index: Int
) : KotlinCallArgument {
    override val isSpread: Boolean get() = false
    override val argumentName: Name? get() = null
}

class ReceiverExpressionKotlinCallArgument private constructor(
    override val receiver: ReceiverValueWithSmartCastInfo,
    override val isSafeCall: Boolean = false,
    val isForImplicitInvoke: Boolean = false
) : ExpressionKotlinCallArgument {
    override val isSpread: Boolean get() = false
    override val argumentName: Name? get() = null
    override fun toString() = "$receiver" + if (isSafeCall) "?" else ""

    companion object {
        // we create ReceiverArgument and fix capture types
        operator fun invoke(
            receiver: ReceiverValueWithSmartCastInfo,
            isSafeCall: Boolean = false,
            isForImplicitInvoke: Boolean = false
        ) = ReceiverExpressionKotlinCallArgument(receiver.prepareReceiverRegardingCaptureTypes(), isSafeCall, isForImplicitInvoke)
    }
}
