/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.smartcasts

import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType

fun getReceiverValueWithSmartCast(
    receiverArgument: ReceiverValue?,
    smartCastType: KotlinType?
) = smartCastType?.let { type -> SmartCastReceiverValue(type, original = null) } ?: receiverArgument

private class SmartCastReceiverValue(private val type: KotlinType, original: SmartCastReceiverValue?) : ReceiverValue {
    private val original = original ?: this

    override fun getType() = type
    override fun replaceType(newType: KotlinType) = SmartCastReceiverValue(newType, original)
    override fun getOriginal() = original
}
