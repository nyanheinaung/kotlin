/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.types.TypeSubstitutor


interface ClassConstructorDescriptor : ConstructorDescriptor {
    override fun getContainingDeclaration(): ClassDescriptor

    override fun getOriginal(): ClassConstructorDescriptor

    override fun substitute(substitutor: TypeSubstitutor): ClassConstructorDescriptor?

    override fun copy(
        newOwner: DeclarationDescriptor,
        modality: Modality,
        visibility: Visibility,
        kind: CallableMemberDescriptor.Kind,
        copyOverrides: Boolean
    ): ClassConstructorDescriptor
}