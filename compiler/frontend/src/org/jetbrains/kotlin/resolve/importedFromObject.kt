/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.types.TypeSubstitutor

fun FunctionDescriptor.asImportedFromObject(original: FunctionImportedFromObject? = null) =
    FunctionImportedFromObject(this, original)

fun PropertyDescriptor.asImportedFromObject(original: PropertyImportedFromObject? = null) =
    PropertyImportedFromObject(this, original)

abstract class ImportedFromObjectCallableDescriptor<out TCallable : CallableMemberDescriptor>(
    val callableFromObject: TCallable,
    private val originalOrNull: TCallable?
) : CallableDescriptor {

    val containingObject = callableFromObject.containingDeclaration as ClassDescriptor

    protected val _original
        get() = originalOrNull ?: this
}

// members imported from object should be wrapped to not require dispatch receiver
class FunctionImportedFromObject(
    functionFromObject: FunctionDescriptor,
    originalOrNull: FunctionDescriptor? = null
) : ImportedFromObjectCallableDescriptor<FunctionDescriptor>(functionFromObject, originalOrNull),
    FunctionDescriptor by functionFromObject {

    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? = null

    override fun substitute(substitutor: TypeSubstitutor) =
        callableFromObject.substitute(substitutor)?.asImportedFromObject(this)

    override fun getOriginal() = _original as FunctionDescriptor

    override fun copy(
        newOwner: DeclarationDescriptor?, modality: Modality?, visibility: Visibility?,
        kind: CallableMemberDescriptor.Kind?, copyOverrides: Boolean
    ): FunctionDescriptor {
        throw UnsupportedOperationException("copy() should not be called on ${this::class.java.simpleName}, was called for $this")
    }
}

class PropertyImportedFromObject(
    propertyFromObject: PropertyDescriptor,
    originalOrNull: PropertyDescriptor? = null
) : ImportedFromObjectCallableDescriptor<PropertyDescriptor>(propertyFromObject, originalOrNull),
    PropertyDescriptor by propertyFromObject {

    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? = null

    override fun substitute(substitutor: TypeSubstitutor) = callableFromObject.substitute(substitutor)?.asImportedFromObject(this)

    override fun getOriginal() = _original as PropertyDescriptor

    override fun copy(
        newOwner: DeclarationDescriptor?, modality: Modality?, visibility: Visibility?,
        kind: CallableMemberDescriptor.Kind?, copyOverrides: Boolean
    ): FunctionDescriptor {
        throw UnsupportedOperationException("copy() should not be called on ${this::class.java.simpleName}, was called for $this")
    }
}

