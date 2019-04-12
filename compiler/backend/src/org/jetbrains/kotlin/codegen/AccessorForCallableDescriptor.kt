/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor

interface AccessorForCallableDescriptor<T : CallableMemberDescriptor> {
    val calleeDescriptor: T

    val superCallTarget: ClassDescriptor?

    val accessorKind: AccessorKind
}
