/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.descriptors

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.psi.KtFile

fun ModuleDescriptor.findPackageFragmentForFile(ktFile: KtFile): PackageFragmentDescriptor? =
    getPackage(ktFile.packageFqName).fragments
        .filterIsInstance<LazyPackageDescriptor>()
        .firstOrNull { it.declarationProvider.containsFile(ktFile) }
