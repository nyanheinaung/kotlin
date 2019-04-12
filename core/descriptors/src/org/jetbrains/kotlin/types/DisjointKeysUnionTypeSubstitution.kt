/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.descriptors.annotations.Annotations

class DisjointKeysUnionTypeSubstitution private constructor(
    private val first: TypeSubstitution,
    private val second: TypeSubstitution
) : TypeSubstitution() {
    companion object {
        @JvmStatic fun create(first: TypeSubstitution, second: TypeSubstitution): TypeSubstitution {
            if (first.isEmpty()) return second
            if (second.isEmpty()) return first

            return DisjointKeysUnionTypeSubstitution(first, second)
        }
    }

    override fun get(key: KotlinType) = first[key] ?: second[key]
    override fun prepareTopLevelType(topLevelType: KotlinType, position: Variance) =
            second.prepareTopLevelType(first.prepareTopLevelType(topLevelType, position), position)

    override fun isEmpty() = false

    override fun approximateCapturedTypes() = first.approximateCapturedTypes() || second.approximateCapturedTypes()
    override fun approximateContravariantCapturedTypes() = first.approximateContravariantCapturedTypes() || second.approximateContravariantCapturedTypes()

    override fun filterAnnotations(annotations: Annotations) = second.filterAnnotations(first.filterAnnotations(annotations))
}
