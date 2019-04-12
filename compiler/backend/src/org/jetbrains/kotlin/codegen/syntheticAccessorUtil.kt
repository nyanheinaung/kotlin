/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.DescriptorUtils

enum class AccessorKind(val suffix: String) {
    NORMAL("p"),
    IN_CLASS_COMPANION("cp"),
    FIELD_FROM_LOCAL("lp"),
    LATEINIT_INTRINSIC("li"),
    JVM_DEFAULT_COMPATIBILITY("jd")
}

private fun CallableMemberDescriptor.getJvmName() =
    DescriptorUtils.getJvmName(this) ?: name.asString()

fun getAccessorNameSuffix(
    descriptor: CallableMemberDescriptor, superCallDescriptor: ClassDescriptor?, accessorKind: AccessorKind
): String {
    if (accessorKind == AccessorKind.JVM_DEFAULT_COMPATIBILITY) return descriptor.getJvmName() + "$" + AccessorKind.JVM_DEFAULT_COMPATIBILITY.suffix

    val suffix = when (descriptor) {
        is ConstructorDescriptor ->
            return "will be ignored"
        is SimpleFunctionDescriptor ->
            descriptor.getJvmName()
        is PropertyDescriptor ->
            descriptor.getJvmName() + "$" + accessorKind.suffix
        else ->
            throw UnsupportedOperationException("Do not know how to create accessor for descriptor $descriptor")
    }
    return if (superCallDescriptor == null) suffix else "$suffix\$s${superCallDescriptor.name.asString().hashCode()}"
}
