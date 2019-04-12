/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.idea

import kotlinx.android.extensions.CacheImplementation
import org.jetbrains.kotlin.android.synthetic.codegen.AbstractAndroidExtensionsExpressionCodegenExtension
import org.jetbrains.kotlin.idea.caches.project.getModuleInfo
import org.jetbrains.kotlin.psi.KtElement

class IDEAndroidExtensionsExpressionCodegenExtension : AbstractAndroidExtensionsExpressionCodegenExtension() {
    override fun isExperimental(element: KtElement?) =
            element?.getModuleInfo()?.androidExtensionsIsExperimental ?: false

    override fun isEnabled(element: KtElement?) =
            element?.getModuleInfo()?.androidExtensionsIsEnabled ?: false

    override fun getGlobalCacheImpl(element: KtElement?) =
            element?.getModuleInfo()?.androidExtensionsGlobalCacheImpl ?: CacheImplementation.DEFAULT
}