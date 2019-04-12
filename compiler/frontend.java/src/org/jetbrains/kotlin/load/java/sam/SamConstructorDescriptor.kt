/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.sam

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.synthetic.SyntheticMemberDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaClassDescriptor
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindExclude

interface SamConstructorDescriptor : SimpleFunctionDescriptor, SyntheticMemberDescriptor<JavaClassDescriptor>

class SamConstructorDescriptorImpl(
        containingDeclaration: DeclarationDescriptor,
        private val samInterface: JavaClassDescriptor
) : SimpleFunctionDescriptorImpl(
        containingDeclaration,
        null,
        samInterface.annotations,
        samInterface.name,
        CallableMemberDescriptor.Kind.SYNTHESIZED,
        samInterface.source
), SamConstructorDescriptor {
    override val baseDescriptorForSynthetic: JavaClassDescriptor
        get() = samInterface
}

object SamConstructorDescriptorKindExclude : DescriptorKindExclude() {
    override fun excludes(descriptor: DeclarationDescriptor) = descriptor is SamConstructorDescriptor

    override val fullyExcludedDescriptorKinds: Int get() = 0
}
