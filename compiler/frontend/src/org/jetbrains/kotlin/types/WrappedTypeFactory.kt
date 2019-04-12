/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.storage.StorageManager

open class WrappedTypeFactory(private val storageManager: StorageManager) {
    open fun createDeferredType(trace: BindingTrace, computation: () -> KotlinType): KotlinType =
        DeferredType.create(storageManager, trace, computation)

    open fun createRecursionIntolerantDeferredType(trace: BindingTrace, computation: () -> KotlinType): KotlinType =
        DeferredType.createRecursionIntolerant(storageManager, trace, computation)
}
