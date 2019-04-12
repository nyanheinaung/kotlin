/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model

import org.jetbrains.kotlin.types.KotlinType

/**
 * Generic abstraction of static information about some part of program.
 */
interface Computation {
    /**
     * Return-type of corresponding part of program.
     * If type is unknown or computation doesn't have a type (e.g. if
     * it is some construction, like "for"-loop), then type is 'null'
     */
    val type: KotlinType?

    /**
     * List of all possible effects of this computation.
     * Note that it's not guaranteed to be complete, i.e. if list
     * doesn't mention some effect, then it should be interpreted
     * as the absence of information about that effect.
     */
    val effects: List<ESEffect>
}