/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.codegen

import kotlinx.android.extensions.CacheImplementation
import org.jetbrains.kotlin.psi.KtElement

class CliAndroidOnDestroyClassBuilderInterceptorExtension(
        private val globalCacheImpl: CacheImplementation
) : AbstractAndroidOnDestroyClassBuilderInterceptorExtension() {
    override fun getGlobalCacheImpl(element: KtElement) = globalCacheImpl
}