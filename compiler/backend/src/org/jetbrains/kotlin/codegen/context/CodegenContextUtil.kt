/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context

import org.jetbrains.kotlin.codegen.OwnerKind
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.resolve.isGetterOfUnderlyingPropertyOfInlineClass
import org.jetbrains.kotlin.resolve.isInlineClass
import org.jetbrains.org.objectweb.asm.Type

object CodegenContextUtil {
    @JvmStatic
    fun getImplementationOwnerClassType(owner: CodegenContext<*>): Type? =
        when (owner) {
            is MultifileClassFacadeContext -> owner.filePartType
            is DelegatingToPartContext -> owner.implementationOwnerClassType
            else -> null
        }

    @JvmStatic
    fun isImplementationOwner(owner: CodegenContext<*>, descriptor: DeclarationDescriptor): Boolean {
        if (descriptor is CallableDescriptor && descriptor.containingDeclaration.isInlineClass()) {
            val isInErasedMethod = owner.contextKind == OwnerKind.ERASED_INLINE_CLASS

            if (descriptor.isGetterOfUnderlyingPropertyOfInlineClass()) {
                return !isInErasedMethod
            }

            when (descriptor) {
                is FunctionDescriptor -> return isInErasedMethod
                is PropertyDescriptor -> return !isInErasedMethod
            }
        }

        return owner !is MultifileClassFacadeContext
    }
}
