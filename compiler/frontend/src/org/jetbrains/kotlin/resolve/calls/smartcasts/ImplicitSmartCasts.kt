/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.smartcasts

import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitReceiver
import org.jetbrains.kotlin.types.KotlinType

data class ImplicitSmartCasts private constructor(val receiverTypes: Map<ImplicitReceiver, KotlinType>) {
    operator fun plus(other: ImplicitSmartCasts) = ImplicitSmartCasts(receiverTypes + other.receiverTypes)

    constructor(receiver: ImplicitReceiver, type: KotlinType) : this(mapOf(receiver to type))
}