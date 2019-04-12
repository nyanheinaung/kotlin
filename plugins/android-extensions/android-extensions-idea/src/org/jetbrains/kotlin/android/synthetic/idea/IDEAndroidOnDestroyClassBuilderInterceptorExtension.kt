/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.idea

import org.jetbrains.kotlin.android.synthetic.codegen.AbstractAndroidOnDestroyClassBuilderInterceptorExtension
import org.jetbrains.kotlin.idea.caches.project.getModuleInfo
import org.jetbrains.kotlin.psi.KtElement

class IDEAndroidOnDestroyClassBuilderInterceptorExtension : AbstractAndroidOnDestroyClassBuilderInterceptorExtension() {
    override fun getGlobalCacheImpl(element: KtElement) = element.getModuleInfo().androidExtensionsGlobalCacheImpl
}