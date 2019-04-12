/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.util

import org.jetbrains.kotlin.builtins.isBuiltinExtensionFunctionalType
import org.jetbrains.kotlin.builtins.isBuiltinFunctionalType
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection

fun createValueParametersForInvokeInFunctionType(
    functionDescriptor: FunctionDescriptor, parameterTypes: List<TypeProjection>
): List<ValueParameterDescriptor> {
    return parameterTypes.mapIndexed { i, typeProjection ->
        ValueParameterDescriptorImpl(
            functionDescriptor, null, i, Annotations.EMPTY,
            Name.identifier("p${i + 1}"), typeProjection.type,
            /* declaresDefaultValue = */ false,
            /* isCrossinline = */ false,
            /* isNoinline = */ false,
            null, SourceElement.NO_SOURCE
        )
    }
}

fun getValueParametersCountFromFunctionType(type: KotlinType): Int {
    assert(type.isBuiltinFunctionalType) { "Not a function type: $type" }
    // Function type arguments = receiver? + parameters + return-type
    return type.arguments.size - (if (type.isBuiltinExtensionFunctionalType) 1 else 0) - 1
}
