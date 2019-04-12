/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.extensions

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.TargetPlatform

interface StorageComponentContainerContributor {
    companion object : ProjectExtensionDescriptor<StorageComponentContainerContributor>(
        "org.jetbrains.kotlin.storageComponentContainerContributor", StorageComponentContainerContributor::class.java
    )

    fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {}
}