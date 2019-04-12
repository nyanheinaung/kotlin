/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter.markers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.idea.caches.project.implementedDescriptors
import org.jetbrains.kotlin.idea.caches.resolve.findModuleDescriptor
import org.jetbrains.kotlin.idea.core.toDescriptor
import org.jetbrains.kotlin.idea.util.expectedDeclarationIfAny
import org.jetbrains.kotlin.idea.util.hasDeclarationOf
import org.jetbrains.kotlin.psi.KtDeclaration

fun getExpectedDeclarationTooltip(declaration: KtDeclaration): String? {
    val descriptor = declaration.toDescriptor() as? MemberDescriptor ?: return null
    val platformModuleDescriptor = declaration.containingKtFile.findModuleDescriptor()

    val commonModuleDescriptors = platformModuleDescriptor.implementedDescriptors
    if (!commonModuleDescriptors.any { it.hasDeclarationOf(descriptor) }) return null

    return "Has declaration in common module"
}

fun KtDeclaration.allNavigatableExpectedDeclarations(): List<KtDeclaration> =
    listOfNotNull(expectedDeclarationIfAny()) + findMarkerBoundDeclarations().mapNotNull { it.expectedDeclarationIfAny() }

fun KtDeclaration.navigateToExpectedTitle() = "Choose expected for $name"

fun KtDeclaration.navigateToExpectedUsagesTitle() = "Expected for $name"

fun buildNavigateToExpectedDeclarationsPopup(element: PsiElement?): NavigationPopupDescriptor? {
    return element?.markerDeclaration?.let {
        val navigatableExpectedDeclarations = it.allNavigatableExpectedDeclarations()
        if (navigatableExpectedDeclarations.isEmpty()) return null
        return NavigationPopupDescriptor(
            navigatableExpectedDeclarations,
            it.navigateToExpectedTitle(),
            it.navigateToExpectedUsagesTitle(),
            ActualExpectedPsiElementCellRenderer()
        )
    }
}