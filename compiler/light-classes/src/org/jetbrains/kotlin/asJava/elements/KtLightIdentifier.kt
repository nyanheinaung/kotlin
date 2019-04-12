/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.elements

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiCompiledElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.light.LightIdentifier
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

open class KtLightIdentifier(
    private val lightOwner: PsiElement,
    private val ktDeclaration: KtNamedDeclaration?
) : LightIdentifier(lightOwner.manager, ktDeclaration?.name ?: ""), PsiCompiledElement {
    val origin: PsiElement?
        get() = when (ktDeclaration) {
            is KtSecondaryConstructor -> ktDeclaration.getConstructorKeyword()
            is KtPrimaryConstructor -> ktDeclaration.getConstructorKeyword()
                                       ?: ktDeclaration.valueParameterList
                                       ?: ktDeclaration.containingClassOrObject?.nameIdentifier
            else -> ktDeclaration?.nameIdentifier
        }

    override fun getMirror() = ((lightOwner as? KtLightElement<*, *>)?.clsDelegate as? PsiNameIdentifierOwner)?.nameIdentifier

    override fun isPhysical() = true
    override fun getParent() = lightOwner
    override fun getContainingFile() = lightOwner.containingFile
    override fun getTextRange() = origin?.textRange ?: TextRange.EMPTY_RANGE
}