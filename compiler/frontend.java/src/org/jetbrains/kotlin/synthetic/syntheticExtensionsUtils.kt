/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.synthetic

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.descriptors.JavaCallableMemberDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaClassDescriptor
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

fun FunctionDescriptor.hasJavaOriginInHierarchy(): Boolean {
    return if (original.overriddenDescriptors.isEmpty())
        this is JavaCallableMemberDescriptor || containingDeclaration is JavaClassDescriptor
    else
        original.overriddenDescriptors.any { it.hasJavaOriginInHierarchy() }
}

fun Visibility.isVisibleOutside() = this != Visibilities.PRIVATE && this != Visibilities.PRIVATE_TO_THIS && this != Visibilities.INVISIBLE_FAKE

fun syntheticVisibility(originalDescriptor: DeclarationDescriptorWithVisibility, isUsedForExtension: Boolean): Visibility {
    val originalVisibility = originalDescriptor.visibility
    return when (originalVisibility) {
        Visibilities.PUBLIC -> Visibilities.PUBLIC

        else -> object : Visibility(originalVisibility.name, originalVisibility.isPublicAPI) {
            override fun isVisible(
                    receiver: ReceiverValue?,
                    what: DeclarationDescriptorWithVisibility,
                    from: DeclarationDescriptor
            ) = originalVisibility.isVisible(
                    if (isUsedForExtension) Visibilities.ALWAYS_SUITABLE_RECEIVER else receiver, originalDescriptor, from)

            override fun mustCheckInImports()
                    = throw UnsupportedOperationException("Should never be called for this visibility")

            override fun normalize()
                    = originalVisibility.normalize()

            override val displayName: String
                get() = originalVisibility.displayName + " for synthetic extension"
        }
    }

}
