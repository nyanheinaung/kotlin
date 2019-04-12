/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.extensions

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.storage.StorageManager

interface PackageFragmentProviderExtension {
    companion object : ProjectExtensionDescriptor<PackageFragmentProviderExtension>(
            "org.jetbrains.kotlin.packageFragmentProviderExtension",
            PackageFragmentProviderExtension::class.java
    )

    fun getPackageFragmentProvider(
            project: Project,
            module: ModuleDescriptor,
            storageManager: StorageManager,
            trace: BindingTrace,
            moduleInfo: ModuleInfo?,
            lookupTracker: LookupTracker
    ): PackageFragmentProvider?
}
