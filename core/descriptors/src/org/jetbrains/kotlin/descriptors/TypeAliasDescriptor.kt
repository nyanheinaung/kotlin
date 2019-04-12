/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor
import org.jetbrains.kotlin.types.SimpleType

interface TypeAliasDescriptor : ClassifierDescriptorWithTypeParameters {
    /// Right-hand side of the type alias definition.
    /// May contain type aliases.
    val underlyingType: SimpleType

    /// Fully expanded type with non-substituted type parameters.
    /// May not contain type aliases.
    val expandedType: SimpleType

    val classDescriptor: ClassDescriptor?

    override fun getOriginal(): TypeAliasDescriptor

    val constructors: Collection<TypeAliasConstructorDescriptor>
}
