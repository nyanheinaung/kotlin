/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.smartcasts

import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.types.KotlinType

interface ExplicitSmartCasts {
    fun type(call: Call?): KotlinType?

    val defaultType: KotlinType?

    operator fun plus(smartCast: SingleSmartCast): ExplicitSmartCasts
}

data class SingleSmartCast(val call: Call?, val type: KotlinType) : ExplicitSmartCasts {
    override fun type(call: Call?) = if (call == this.call) type else null

    override val defaultType: KotlinType get() = type

    override fun plus(smartCast: SingleSmartCast) =
        if (this == smartCast) this
        else MultipleSmartCasts(mapOf(call to type, smartCast.call to smartCast.type))
}

data class MultipleSmartCasts internal constructor(val map: Map<Call?, KotlinType>) : ExplicitSmartCasts {
    override fun type(call: Call?) = map[call]

    override val defaultType: KotlinType? get() = null

    override fun plus(smartCast: SingleSmartCast) = MultipleSmartCasts(map + mapOf(smartCast.call to smartCast.type))
}