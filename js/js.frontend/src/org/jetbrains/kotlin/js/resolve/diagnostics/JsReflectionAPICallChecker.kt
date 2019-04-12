/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.ReflectionTypes
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.NotFoundClasses
import org.jetbrains.kotlin.diagnostics.Errors.UNSUPPORTED
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.checkers.AbstractReflectionApiCallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue

private val ALLOWED_KCLASS_MEMBERS = setOf("simpleName", "isInstance")

class JsReflectionAPICallChecker(
        module: ModuleDescriptor,
        private val reflectionTypes: ReflectionTypes,
        notFoundClasses: NotFoundClasses,
        storageManager: StorageManager
) : AbstractReflectionApiCallChecker(module, notFoundClasses, storageManager) {
    override val isWholeReflectionApiAvailable: Boolean
        get() = false

    override fun report(element: PsiElement, context: CallCheckerContext) {
        context.trace.report(UNSUPPORTED.on(element, "This reflection API is not supported yet in JavaScript"))
    }

    private val kClass by storageManager.createLazyValue { reflectionTypes.kClass }

    override fun isAllowedReflectionApi(descriptor: CallableDescriptor, containingClass: ClassDescriptor): Boolean =
            super.isAllowedReflectionApi(descriptor, containingClass) ||
            DescriptorUtils.isSubclass(containingClass, kClass) && descriptor.name.asString() in ALLOWED_KCLASS_MEMBERS
}
