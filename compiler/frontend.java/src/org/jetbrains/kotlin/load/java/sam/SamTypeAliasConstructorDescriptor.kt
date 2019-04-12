/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.sam

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithNavigationSubstitute
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.load.java.descriptors.JavaClassDescriptor

interface SamTypeAliasConstructorDescriptor : SamConstructorDescriptor, DeclarationDescriptorWithNavigationSubstitute {
    val typeAliasDescriptor: TypeAliasDescriptor

    override val substitute: DeclarationDescriptor
        get() = typeAliasDescriptor
}

class SamTypeAliasConstructorDescriptorImpl(
    override val typeAliasDescriptor: TypeAliasDescriptor,
    private val samInterfaceConstructorDescriptor: SamConstructorDescriptor
) : SimpleFunctionDescriptorImpl(
        typeAliasDescriptor.containingDeclaration,
        null,
        samInterfaceConstructorDescriptor.baseDescriptorForSynthetic.annotations,
        typeAliasDescriptor.name,
        CallableMemberDescriptor.Kind.SYNTHESIZED,
        typeAliasDescriptor.source
), SamTypeAliasConstructorDescriptor {
    override val baseDescriptorForSynthetic: JavaClassDescriptor
        get() = samInterfaceConstructorDescriptor.baseDescriptorForSynthetic
}
