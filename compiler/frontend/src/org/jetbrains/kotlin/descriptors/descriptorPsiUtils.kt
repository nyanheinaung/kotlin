/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.descriptors.impl.SyntheticFieldDescriptor
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils

fun PropertyAccessorDescriptor.hasBody(): Boolean {
    val ktAccessor = DescriptorToSourceUtils.getSourceFromDescriptor(this) as? KtDeclarationWithBody
    return ktAccessor != null && ktAccessor.hasBody()
}

fun isBackingFieldReference(descriptor: DeclarationDescriptor?): Boolean {
    return descriptor is SyntheticFieldDescriptor
}