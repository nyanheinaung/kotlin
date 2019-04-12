/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.completion

import com.intellij.openapi.extensions.ExtensionPointName
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

interface CompletionInformationProvider {
    companion object {
        val EP_NAME: ExtensionPointName<CompletionInformationProvider> =
                ExtensionPointName.create("org.jetbrains.kotlin.completionInformationProvider")
    }

    fun getContainerAndReceiverInformation(descriptor: DeclarationDescriptor): String?
}