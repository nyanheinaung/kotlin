/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.tail
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.ResolveSessionUtils

abstract class AbstractJavaResolverCache(private val resolveSession: ResolveSession) : JavaResolverCache {

    protected val trace: BindingTrace get() = resolveSession.trace

    override fun getClassResolvedFromSource(fqName: FqName): ClassDescriptor? {
        return trace.get(BindingContext.FQNAME_TO_CLASS_DESCRIPTOR, fqName.toUnsafe())?.takeUnless { it.isExpect }
            ?: findInPackageFragments(fqName)
    }

    private fun findInPackageFragments(fullFqName: FqName): ClassDescriptor? {
        var fqName = if (fullFqName.isRoot) fullFqName else fullFqName.parent()

        while (true) {
            val packageDescriptor = resolveSession.getPackageFragment(fqName)
            if (packageDescriptor != null) {
                val result = ResolveSessionUtils.findClassByRelativePath(packageDescriptor.getMemberScope(), fullFqName.tail(fqName))
                if (result != null) return result
            }

            if (fqName.isRoot) break
            fqName = fqName.parent()
        }

        return null
    }

}