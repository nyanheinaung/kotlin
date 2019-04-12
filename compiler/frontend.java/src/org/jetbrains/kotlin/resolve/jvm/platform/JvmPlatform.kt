/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.platform

import org.jetbrains.kotlin.builtins.jvm.JvmBuiltIns
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager

object JvmPlatform : TargetPlatform("JVM") {
    override fun computePlatformSpecificDefaultImports(storageManager: StorageManager, result: MutableList<ImportPath>) {
        result.add(ImportPath.fromString("kotlin.jvm.*"))

        fun addAllClassifiersFromScope(scope: MemberScope) {
            for (descriptor in scope.getContributedDescriptors(DescriptorKindFilter.CLASSIFIERS, MemberScope.ALL_NAME_FILTER)) {
                result.add(ImportPath(DescriptorUtils.getFqNameSafe(descriptor), false))
            }
        }

        for (builtInPackage in JvmBuiltIns(storageManager, JvmBuiltIns.Kind.FROM_CLASS_LOADER).builtInPackagesImportedByDefault) {
            addAllClassifiersFromScope(builtInPackage.memberScope)
        }
    }

    override val defaultLowPriorityImports: List<ImportPath> = listOf(ImportPath.fromString("java.lang.*"))

    override val platformConfigurator: PlatformConfigurator = JvmPlatformConfigurator

    override val multiTargetPlatform = MultiTargetPlatform.Specific(platformName)
}
