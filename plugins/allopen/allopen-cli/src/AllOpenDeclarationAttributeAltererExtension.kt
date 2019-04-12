/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.allopen

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.extensions.AnnotationBasedExtension
import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext

class CliAllOpenDeclarationAttributeAltererExtension(
        private val allOpenAnnotationFqNames: List<String>
) : AbstractAllOpenDeclarationAttributeAltererExtension() {
    override fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?) = allOpenAnnotationFqNames
}

abstract class AbstractAllOpenDeclarationAttributeAltererExtension : DeclarationAttributeAltererExtension, AnnotationBasedExtension {
    companion object {
        val ANNOTATIONS_FOR_TESTS = listOf("AllOpen", "AllOpen2", "test.AllOpen")
    }

    override fun refineDeclarationModality(
            modifierListOwner: KtModifierListOwner,
            declaration: DeclarationDescriptor?,
            containingDeclaration: DeclarationDescriptor?,
            currentModality: Modality,
            bindingContext: BindingContext,
            isImplicitModality: Boolean
    ): Modality? {
        if (currentModality != Modality.FINAL) {
            return null
        }

        val descriptor = declaration as? ClassDescriptor ?: containingDeclaration ?: return null
        if (descriptor.hasSpecialAnnotation(modifierListOwner)) {
            return if (!isImplicitModality && modifierListOwner.hasModifier(KtTokens.FINAL_KEYWORD))
                Modality.FINAL // Explicit final
            else
                Modality.OPEN
        }

        return null
    }
}