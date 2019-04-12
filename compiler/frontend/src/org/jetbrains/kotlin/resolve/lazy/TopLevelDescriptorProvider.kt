/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyPackageDescriptor

interface TopLevelDescriptorProvider {
    fun getPackageFragment(fqName: FqName): LazyPackageDescriptor?

    fun getPackageFragmentOrDiagnoseFailure(fqName: FqName, from: KtFile?): LazyPackageDescriptor

    fun getTopLevelClassifierDescriptors(fqName: FqName, location: LookupLocation): Collection<ClassifierDescriptor>

    fun assertValid()
}

object NoTopLevelDescriptorProvider : TopLevelDescriptorProvider {
    private fun shouldNotBeCalled(): Nothing = throw UnsupportedOperationException("Should not be called")

    override fun getPackageFragment(fqName: FqName): LazyPackageDescriptor? {
        shouldNotBeCalled()
    }

    override fun getPackageFragmentOrDiagnoseFailure(fqName: FqName, from: KtFile?): LazyPackageDescriptor {
        shouldNotBeCalled()
    }

    override fun getTopLevelClassifierDescriptors(fqName: FqName, location: LookupLocation): Collection<ClassifierDescriptor> {
        shouldNotBeCalled()
    }

    override fun assertValid() {
        shouldNotBeCalled()
    }
}
