/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.util

import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.impl.DescriptorDerivedFromTypeAlias

class FakeCallableDescriptorForTypeAliasObject(override val typeAliasDescriptor: TypeAliasDescriptor) :
    FakeCallableDescriptorForObject(typeAliasDescriptor.classDescriptor!!),
    DescriptorDerivedFromTypeAlias {
    override fun getReferencedDescriptor() =
        typeAliasDescriptor

    override fun equals(other: Any?): Boolean =
        other is FakeCallableDescriptorForTypeAliasObject &&
                typeAliasDescriptor == other.typeAliasDescriptor

    override fun hashCode(): Int =
        typeAliasDescriptor.hashCode()
}
