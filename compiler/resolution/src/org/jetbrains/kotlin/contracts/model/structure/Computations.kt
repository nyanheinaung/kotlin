/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.structure

import org.jetbrains.kotlin.contracts.model.Computation
import org.jetbrains.kotlin.contracts.model.ESEffect
import org.jetbrains.kotlin.types.KotlinType

class CallComputation(override val type: KotlinType?, override val effects: List<ESEffect>) : Computation

object UNKNOWN_COMPUTATION : Computation {
    override val type: KotlinType? = null
    override val effects: List<ESEffect> = emptyList()
}
