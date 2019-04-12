/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.compiler.resolve

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.hasBackingField
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter

class SerializableProperties(private val serializableClass: ClassDescriptor, val bindingContext: BindingContext) {
    private val primaryConstructorParameters: List<ValueParameterDescriptor> =
        serializableClass.unsubstitutedPrimaryConstructor?.valueParameters ?: emptyList()

    private val primaryConstructorProperties: Map<PropertyDescriptor, Boolean> =
        primaryConstructorParameters.asSequence()
            .map { parameter -> bindingContext[BindingContext.VALUE_PARAMETER_AS_PROPERTY, parameter] to parameter.declaresDefaultValue() }
            .mapNotNull { (a, b) -> if (a == null) null else a to b }
            .toMap()

    val isExternallySerializable: Boolean =
        primaryConstructorParameters.size == primaryConstructorProperties.size

    val serializableProperties: List<SerializableProperty> =
        serializableClass.unsubstitutedMemberScope.getContributedDescriptors(DescriptorKindFilter.VARIABLES)
            .asSequence()
            .filterIsInstance<PropertyDescriptor>()
            .filter { it.kind == CallableMemberDescriptor.Kind.DECLARATION }
            .filter(this::isPropSerializable)
            .map { prop -> SerializableProperty(prop, primaryConstructorProperties[prop] ?: false, prop.hasBackingField(bindingContext)) }
            .filterNot { it.transient }
            .partition { primaryConstructorProperties.contains(it.descriptor) }
            .run {
                val supers = serializableClass.getSuperClassNotAny()
                if (supers == null || !supers.isInternalSerializable)
                    first + second
                else
                    SerializableProperties(supers, bindingContext).serializableProperties + first + second
            }
            .also(::validateUniqueSerialNames)

    private fun validateUniqueSerialNames(props: List<SerializableProperty>) {
        val namesSet = mutableSetOf<String>()
        props.forEach {
            if (!namesSet.add(it.name)) throw IllegalStateException("$serializableClass has duplicate serial name of property ${it.name}, either in it or its parents.")
        }
    }


    private fun isPropSerializable(it: PropertyDescriptor) =
        if (serializableClass.isInternalSerializable) !it.annotations.serialTransient
        else !Visibilities.isPrivate(it.visibility) && ((it.isVar && !it.annotations.serialTransient) || primaryConstructorProperties.contains(
            it
        ))

    val serializableConstructorProperties: List<SerializableProperty> =
        serializableProperties.asSequence()
            .filter { primaryConstructorProperties.contains(it.descriptor) }
            .toList()

    val serializableStandaloneProperties: List<SerializableProperty> =
        serializableProperties.minus(serializableConstructorProperties)

    val size = serializableProperties.size
    operator fun get(index: Int) = serializableProperties[index]
    operator fun iterator() = serializableProperties.iterator()

    val primaryConstructorWithDefaults = serializableClass.unsubstitutedPrimaryConstructor
        ?.original?.valueParameters?.any { it.declaresDefaultValue() } ?: false
}

internal fun List<SerializableProperty>.bitMaskSlotCount() = size / 32 + 1
internal fun bitMaskSlotAt(propertyIndex: Int) = propertyIndex / 32