/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingContextUtils

interface LocalDescriptorResolver {
    fun resolveLocalDeclaration(declaration: KtDeclaration): DeclarationDescriptor
}

class CompilerLocalDescriptorResolver(
    private val lazyDeclarationResolver: LazyDeclarationResolver
) : LocalDescriptorResolver {
    override fun resolveLocalDeclaration(declaration: KtDeclaration): DeclarationDescriptor {
        return lazyDeclarationResolver.resolveToDescriptor(declaration)
    }
}