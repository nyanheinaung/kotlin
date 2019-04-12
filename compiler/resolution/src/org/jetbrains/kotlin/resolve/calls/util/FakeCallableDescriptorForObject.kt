/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.util

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.descriptorUtil.classValueType
import org.jetbrains.kotlin.resolve.descriptorUtil.getClassObjectReferenceTarget
import org.jetbrains.kotlin.resolve.descriptorUtil.hasClassValueDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitutor
import java.util.*

open class FakeCallableDescriptorForObject(
    val classDescriptor: ClassDescriptor
) : DeclarationDescriptorWithVisibility by classDescriptor.getClassObjectReferenceTarget(), VariableDescriptor {

    init {
        assert(classDescriptor.hasClassValueDescriptor) {
            "FakeCallableDescriptorForObject can be created only for objects, classes with companion object or enum entries: $classDescriptor"
        }

    }

    open fun getReferencedDescriptor(): ClassifierDescriptorWithTypeParameters = classDescriptor.getClassObjectReferenceTarget()

    fun getReferencedObject(): ClassDescriptor = classDescriptor.getClassObjectReferenceTarget()

    override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? = null

    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? = null

    override fun getTypeParameters(): List<TypeParameterDescriptor> = Collections.emptyList()

    override fun getValueParameters(): List<ValueParameterDescriptor> = Collections.emptyList()

    override fun getReturnType(): KotlinType? = type

    override fun hasSynthesizedParameterNames() = false

    override fun hasStableParameterNames() = false

    override fun getOverriddenDescriptors(): Set<CallableDescriptor> = Collections.emptySet()

    override fun getType(): KotlinType = classDescriptor.classValueType!!

    override fun isVar() = false

    override fun getOriginal(): CallableDescriptor = this

    override fun getCompileTimeInitializer() = null

    override fun getSource(): SourceElement = classDescriptor.source

    override fun isConst(): Boolean = false

    override fun isLateInit(): Boolean = false

    override fun equals(other: Any?) = other is FakeCallableDescriptorForObject && classDescriptor == other.classDescriptor

    override fun hashCode() = classDescriptor.hashCode()

    override fun getContainingDeclaration() = classDescriptor.getClassObjectReferenceTarget().containingDeclaration

    override fun substitute(substitutor: TypeSubstitutor) = this

    override fun <V> getUserData(key: CallableDescriptor.UserDataKey<V>?): V? = null
}
