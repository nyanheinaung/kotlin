/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.smartcasts

class ConditionalDataFlowInfo(val thenInfo: DataFlowInfo, val elseInfo: DataFlowInfo = thenInfo) {
    fun and(other: ConditionalDataFlowInfo): ConditionalDataFlowInfo = when {
        this == EMPTY -> other
        other == EMPTY -> this
        else -> ConditionalDataFlowInfo(this.thenInfo.and(other.thenInfo), this.elseInfo.and(other.elseInfo))
    }

    fun or(other: ConditionalDataFlowInfo): ConditionalDataFlowInfo = when {
        this == EMPTY -> other
        other == EMPTY -> this
        else -> ConditionalDataFlowInfo(this.thenInfo.or(other.thenInfo), this.elseInfo.or(other.elseInfo))
    }

    companion object {
        val EMPTY = ConditionalDataFlowInfo(DataFlowInfo.EMPTY)
    }
}