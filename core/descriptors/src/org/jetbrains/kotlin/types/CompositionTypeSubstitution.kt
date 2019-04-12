/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor

// Composition works as it's expected: Composition(outer, inner)(type) = outer(inner(type))
// Note that when a substitution returns null it means that type shouldn't be changed (i.e. it works like the identity function in that case)
// Currently it's only works if the inner substitution is a mapping from type parameter to another type parameter.
// Otherwise composition semantics become a little bit complicated.
class CompositionTypeSubstitution(
        private val outer: TypeSubstitution, private val inner: Map<TypeParameterDescriptor, TypeParameterDescriptor>
) : DelegatedTypeSubstitution(outer) {

    override fun get(key: KotlinType) = inner[key.constructor.declarationDescriptor]?.let { outer[it.defaultType] } ?: outer[key]
}
