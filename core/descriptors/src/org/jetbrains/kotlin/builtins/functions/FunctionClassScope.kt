/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.builtins.functions

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.scopes.GivenFunctionsMemberScope
import org.jetbrains.kotlin.storage.StorageManager

class FunctionClassScope(
        storageManager: StorageManager,
        containingClass: FunctionClassDescriptor
) : GivenFunctionsMemberScope(storageManager, containingClass) {
    override fun computeDeclaredFunctions(): List<FunctionDescriptor> =
            when ((containingClass as FunctionClassDescriptor).functionKind) {
                FunctionClassDescriptor.Kind.Function -> listOf(FunctionInvokeDescriptor.create(containingClass, isSuspend = false))
                FunctionClassDescriptor.Kind.SuspendFunction -> listOf(FunctionInvokeDescriptor.create(containingClass, isSuspend = true))
                else -> emptyList()
            }
}
