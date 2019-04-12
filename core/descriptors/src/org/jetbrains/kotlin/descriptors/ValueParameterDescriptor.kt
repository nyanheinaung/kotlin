/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitutor

interface ValueParameterDescriptor : VariableDescriptor, ParameterDescriptor {
    override fun getContainingDeclaration(): CallableDescriptor

    /**
     * Returns the 0-based index of the value parameter in the parameter list of its containing function.

     * @return the parameter index
     */
    val index: Int

    /**
     * @return true iff this parameter belongs to a declared function (not a fake override) and declares the default value,
     * i.e. explicitly specifies it in the function signature. Also see 'hasDefaultValue' extension in DescriptorUtils.kt
     */
    fun declaresDefaultValue(): Boolean

    val varargElementType: KotlinType?

    override fun getOriginal(): ValueParameterDescriptor

    override fun substitute(substitutor: TypeSubstitutor): ValueParameterDescriptor

    fun copy(newOwner: CallableDescriptor, newName: Name, newIndex: Int): ValueParameterDescriptor

    /**
     * Parameter p1 overrides p2 iff
     * a) their respective owners (function declarations) f1 override f2
     * b) p1 and p2 have the same indices in the owners' parameter lists
     */
    override fun getOverriddenDescriptors(): Collection<ValueParameterDescriptor>

    val isCrossinline: Boolean

    val isNoinline: Boolean

    override fun isLateInit(): Boolean = false
}
