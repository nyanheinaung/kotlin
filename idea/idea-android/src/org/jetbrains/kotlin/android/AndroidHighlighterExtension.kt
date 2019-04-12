/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.android.synthetic.res.AndroidSyntheticProperty
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.highlighter.HighlighterExtension
import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingColors

class AndroidHighlighterExtension : HighlighterExtension() {
    override fun highlightDeclaration(
        elementToHighlight: PsiElement,
        descriptor: DeclarationDescriptor
    ) = when (descriptor) {
        is AndroidSyntheticProperty -> KotlinHighlightingColors.ANDROID_EXTENSIONS_PROPERTY_CALL
        else -> null
    }
}
