/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.extensions

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext

interface DeclarationAttributeAltererExtension {
    companion object : ProjectExtensionDescriptor<DeclarationAttributeAltererExtension>(
        "org.jetbrains.kotlin.declarationAttributeAltererExtension",
        DeclarationAttributeAltererExtension::class.java
    )

    /**
     * Returns the new modality for the [declaration], or null if the [currentModality] is good enough.
     */
    fun refineDeclarationModality(
        modifierListOwner: KtModifierListOwner,
        declaration: DeclarationDescriptor?,
        containingDeclaration: DeclarationDescriptor?,
        currentModality: Modality,
        bindingContext: BindingContext,
        isImplicitModality: Boolean
    ): Modality? = null

    @Deprecated(
        "Use refineDeclarationModality(modifierListOwner, declaration, containingDeclaration, currentModality, bindingContext, isImplicitModality)",
        ReplaceWith("refineDeclarationModality(modifierListOwner, declaration, containingDeclaration, currentModality, bindingContext, false)")
    )
    fun refineDeclarationModality(
        modifierListOwner: KtModifierListOwner,
        declaration: DeclarationDescriptor?,
        containingDeclaration: DeclarationDescriptor?,
        currentModality: Modality,
        bindingContext: BindingContext
    ): Modality? {
        return refineDeclarationModality(modifierListOwner, declaration, containingDeclaration, currentModality, bindingContext, false)
    }

    fun shouldConvertFirstSAMParameterToReceiver(function: FunctionDescriptor): Boolean = false
}