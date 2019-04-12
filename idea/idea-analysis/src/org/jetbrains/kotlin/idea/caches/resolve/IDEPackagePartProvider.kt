/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.caches.resolve

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import org.jetbrains.kotlin.idea.vfilefinder.KotlinJvmModuleAnnotationsIndex
import org.jetbrains.kotlin.idea.vfilefinder.KotlinModuleMappingIndex
import org.jetbrains.kotlin.load.kotlin.PackagePartProvider
import org.jetbrains.kotlin.metadata.jvm.deserialization.PackageParts
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.serialization.deserialization.MetadataPartProvider

class IDEPackagePartProvider(val scope: GlobalSearchScope) : PackagePartProvider, MetadataPartProvider {
    override fun findPackageParts(packageFqName: String): List<String> =
        getPackageParts(packageFqName).flatMap(PackageParts::parts).distinct()

    override fun findMetadataPackageParts(packageFqName: String): List<String> =
        getPackageParts(packageFqName).flatMap(PackageParts::metadataParts).distinct()

    private fun getPackageParts(packageFqName: String): MutableList<PackageParts> =
        FileBasedIndex.getInstance().getValues(KotlinModuleMappingIndex.KEY, packageFqName, scope)

    // Note that in case of several modules with the same name, we return all annotations on all of them, which is probably incorrect
    override fun getAnnotationsOnBinaryModule(moduleName: String): List<ClassId> =
        FileBasedIndex.getInstance().getValues(KotlinJvmModuleAnnotationsIndex.KEY, moduleName, scope).flatten()
}
