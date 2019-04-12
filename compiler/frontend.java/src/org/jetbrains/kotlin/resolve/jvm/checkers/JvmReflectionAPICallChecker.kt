/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.NotFoundClasses
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.load.java.JvmAbi
import org.jetbrains.kotlin.resolve.calls.checkers.AbstractReflectionApiCallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm.NO_REFLECTION_IN_CLASS_PATH
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue

/**
 * If there's no Kotlin reflection implementation found in the classpath, checks that there are no usages
 * of reflection API which will fail at runtime.
 */
class JvmReflectionAPICallChecker(
        private val module: ModuleDescriptor,
        notFoundClasses: NotFoundClasses,
        storageManager: StorageManager
) : AbstractReflectionApiCallChecker(module, notFoundClasses, storageManager) {
    override val isWholeReflectionApiAvailable by storageManager.createLazyValue {
        module.findClassAcrossModuleDependencies(JvmAbi.REFLECTION_FACTORY_IMPL) != null
    }

    override fun report(element: PsiElement, context: CallCheckerContext) {
        context.trace.report(NO_REFLECTION_IN_CLASS_PATH.on(element))
    }
}
