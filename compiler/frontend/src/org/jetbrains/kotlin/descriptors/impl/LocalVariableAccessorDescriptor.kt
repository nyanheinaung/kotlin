/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType

sealed class LocalVariableAccessorDescriptor(
    final override val correspondingVariable: LocalVariableDescriptor,
    isGetter: Boolean
) : SimpleFunctionDescriptorImpl(
    correspondingVariable.containingDeclaration,
    null,
    Annotations.EMPTY,
    Name.special((if (isGetter) "<get-" else "<set-") + correspondingVariable.name + ">"),
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    SourceElement.NO_SOURCE
), VariableAccessorDescriptor {
    class Getter(correspondingVariable: LocalVariableDescriptor) : LocalVariableAccessorDescriptor(correspondingVariable, true)
    class Setter(correspondingVariable: LocalVariableDescriptor) : LocalVariableAccessorDescriptor(correspondingVariable, false)

    init {
        val valueParameters =
            if (isGetter) emptyList() else listOf(createValueParameter(Name.identifier("value"), correspondingVariable.type))
        val returnType =
            if (isGetter) correspondingVariable.type else correspondingVariable.builtIns.unitType
        @Suppress("LeakingThis")
        initialize(null, null, emptyList(), valueParameters, returnType, Modality.FINAL, Visibilities.LOCAL)
    }

    private fun createValueParameter(name: Name, type: KotlinType): ValueParameterDescriptorImpl {
        return ValueParameterDescriptorImpl(
            this, null, 0, Annotations.EMPTY, name, type,
            false, false, false, null, SourceElement.NO_SOURCE
        )
    }

}
