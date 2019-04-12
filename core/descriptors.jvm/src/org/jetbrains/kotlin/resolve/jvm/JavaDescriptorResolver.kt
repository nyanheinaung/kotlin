/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.load.java.components.JavaResolverCache
import org.jetbrains.kotlin.load.java.lazy.LazyJavaPackageFragmentProvider
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.LightClassOriginKind

class JavaDescriptorResolver(
    val packageFragmentProvider: LazyJavaPackageFragmentProvider,
    private val javaResolverCache: JavaResolverCache
) {
    fun resolveClass(javaClass: JavaClass): ClassDescriptor? {
        val fqName = javaClass.fqName
        if (fqName != null && javaClass.lightClassOriginKind == LightClassOriginKind.SOURCE) {
            return javaResolverCache.getClassResolvedFromSource(fqName)
        }

        javaClass.outerClass?.let { outerClass ->
            val outerClassScope = resolveClass(outerClass)?.unsubstitutedInnerClassesScope
            return outerClassScope?.getContributedClassifier(javaClass.name, NoLookupLocation.FROM_JAVA_LOADER) as? ClassDescriptor
        }

        if (fqName == null) return null

        return packageFragmentProvider.getPackageFragments(fqName.parent()).firstOrNull()?.findClassifierByJavaClass(javaClass)
    }
}
