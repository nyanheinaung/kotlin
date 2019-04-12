/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.extension

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.types.KotlinType

interface KotlinIndicesHelperExtension {
    companion object : ProjectExtensionDescriptor<KotlinIndicesHelperExtension>(
        "org.jetbrains.kotlin.kotlinIndicesHelperExtension", KotlinIndicesHelperExtension::class.java
    )

    fun appendExtensionCallables(
        consumer: MutableList<in CallableDescriptor>,
        moduleDescriptor: ModuleDescriptor,
        receiverTypes: Collection<KotlinType>,
        nameFilter: (String) -> Boolean,
        lookupLocation: LookupLocation
    )
}