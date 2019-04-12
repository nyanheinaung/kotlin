/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedMemberScope
import org.jetbrains.kotlin.storage.StorageManager

abstract class DeserializedPackageFragment(
    fqName: FqName,
    protected val storageManager: StorageManager,
    module: ModuleDescriptor
) : PackageFragmentDescriptorImpl(module, fqName) {

    abstract fun initialize(components: DeserializationComponents)

    abstract val classDataFinder: ClassDataFinder

    open fun hasTopLevelClass(name: Name): Boolean {
        val scope = getMemberScope()
        return scope is DeserializedMemberScope && name in scope.classNames
    }
}
