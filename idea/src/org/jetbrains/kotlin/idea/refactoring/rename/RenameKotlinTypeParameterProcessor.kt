/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.rename

import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageInfo
import org.jetbrains.kotlin.idea.caches.resolve.unsafeResolveToDescriptor
import org.jetbrains.kotlin.psi.KtTypeParameter

class RenameKotlinTypeParameterProcessor : RenameKotlinPsiProcessor() {
    override fun canProcessElement(element: PsiElement) = element is KtTypeParameter

    override fun findCollisions(
            element: PsiElement,
            newName: String,
            allRenames: MutableMap<out PsiElement, String>,
            result: MutableList<UsageInfo>
    ) {
        val declaration = element as? KtTypeParameter ?: return
        val descriptor = declaration.unsafeResolveToDescriptor()
        checkRedeclarations(descriptor, newName, result)
    }
}