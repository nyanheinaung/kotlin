/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin

import com.intellij.psi.xml.XmlAttributeValue
import org.jetbrains.kotlin.android.synthetic.res.AndroidSyntheticProperty
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.completion.CompletionInformationProvider
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.source.PsiSourceElement

class AndroidExtensionsCompletionInformationProvider : CompletionInformationProvider {
    override fun getContainerAndReceiverInformation(descriptor: DeclarationDescriptor): String? {
        if (descriptor !is AndroidSyntheticProperty) {
            return null
        }

        val propertyDescriptor = (descriptor as? PropertyDescriptor) ?: return null
        val attributeValue = (propertyDescriptor.source as? PsiSourceElement)?.psi as? XmlAttributeValue ?: return null
        val extensionReceiverType = propertyDescriptor.original.extensionReceiverParameter?.type

        return buildString {
            append(" from ${attributeValue.containingFile.name}")
            extensionReceiverType?.let { append(" for " + DescriptorRenderer.SHORT_NAMES_IN_TYPES.renderType(it)) }
            append(" (Android Extensions)")
        }
    }
}