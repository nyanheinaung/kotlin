/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.structure

import org.jetbrains.kotlin.contracts.description.InvocationKind
import org.jetbrains.kotlin.contracts.model.ESEffect
import org.jetbrains.kotlin.contracts.model.ESValue
import org.jetbrains.kotlin.contracts.model.SimpleEffect

data class ESCalls(val callable: ESValue, val kind: InvocationKind) : SimpleEffect() {
    override fun isImplies(other: ESEffect): Boolean? {
        if (other !is ESCalls) return null

        if (callable != other.callable) return null

        return kind == other.kind
    }

}

data class ESReturns(val value: ESValue) : SimpleEffect() {
    override fun isImplies(other: ESEffect): Boolean? {
        if (other !is ESReturns) return null

        if (this.value !is ESConstant || other.value !is ESConstant) return this.value == other.value

        // ESReturns(x) implies ESReturns(?) for any 'x'
        if (other.value.isWildcard) return true

        return value == other.value
    }
}

inline fun ESEffect.isReturns(block: ESReturns.() -> Boolean): Boolean =
    this is ESReturns && block()
