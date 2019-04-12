/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.resolve.descriptorUtil.hasOnlyInputTypesAnnotation
import org.jetbrains.kotlin.types.KotlinType

class TypeVariable(
    val call: CallHandle,
    internal val freshTypeParameter: TypeParameterDescriptor,
    val originalTypeParameter: TypeParameterDescriptor,
    val isExternal: Boolean
) {
    val name: Name get() = originalTypeParameter.name

    val type: KotlinType get() = freshTypeParameter.defaultType

    fun hasOnlyInputTypesAnnotation(): Boolean =
        originalTypeParameter.hasOnlyInputTypesAnnotation()
}

interface CallHandle {
    object NONE : CallHandle
}

class CallBasedCallHandle(val call: Call) : CallHandle {
    override fun equals(other: Any?) =
        other is CallBasedCallHandle && call === other.call

    override fun hashCode() =
        System.identityHashCode(call)

    override fun toString() =
        call.toString()
}

fun Call.toHandle(): CallHandle = CallBasedCallHandle(this)
