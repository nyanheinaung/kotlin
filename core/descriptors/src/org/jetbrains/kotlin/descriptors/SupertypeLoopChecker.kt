/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor

interface SupertypeLoopChecker {
    fun findLoopsInSupertypesAndDisconnect(
            currentTypeConstructor: TypeConstructor,
            superTypes: Collection<KotlinType>,
            neighbors: (TypeConstructor) -> Iterable<KotlinType>,
            reportLoop: (KotlinType) -> Unit
    ): Collection<KotlinType>

    object EMPTY : SupertypeLoopChecker {
        override fun findLoopsInSupertypesAndDisconnect(
                currentTypeConstructor: TypeConstructor,
                superTypes: Collection<KotlinType>,
                neighbors: (TypeConstructor) -> Iterable<KotlinType>,
                reportLoop: (KotlinType) -> Unit): Collection<KotlinType> = superTypes
    }
}
