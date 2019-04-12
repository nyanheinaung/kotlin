/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

/**
 * Low level abstraction for bounded range that is used to generate contains checks and for loops.
 */
class SimpleBoundedValue(
    instanceType: Type,
    val lowBound: StackValue,
    isLowInclusive: Boolean = true,
    val highBound: StackValue,
    isHighInclusive: Boolean = true
) : AbstractBoundedValue(instanceType, isLowInclusive, isHighInclusive) {

    override fun putHighLow(v: InstructionAdapter, type: Type) {
        if (!lowBound.canHaveSideEffects() || !highBound.canHaveSideEffects()) {
            highBound.put(type, v)
            lowBound.put(type, v)
        } else {
            lowBound.put(type, v)
            highBound.put(type, v)
            AsmUtil.swap(v, type, type)
        }
    }
}