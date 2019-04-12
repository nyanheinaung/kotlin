/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin.declarations

import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.parents
import org.jetbrains.kotlin.asJava.elements.KtLightIdentifier
import org.jetbrains.kotlin.psi.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.kotlin.unwrapFakeFileForLightClass
import org.jetbrains.uast.toUElement

class UastLightIdentifier(lightOwner: PsiNameIdentifierOwner, ktDeclaration: KtNamedDeclaration?) :
    KtLightIdentifier(lightOwner, ktDeclaration) {
    override fun getContainingFile(): PsiFile = unwrapFakeFileForLightClass(super.getContainingFile())
}

class KotlinUIdentifier private constructor(
    override val javaPsi: PsiElement?,
    override val sourcePsi: PsiElement?,
    override val psi: PsiElement?,
    givenParent: UElement?
) : UIdentifier(psi, givenParent) {

    init {
        if (ApplicationManager.getApplication().isUnitTestMode && !acceptableSourcePsi(sourcePsi))
            throw AssertionError("sourcePsi should be physical leaf element but got $sourcePsi of (${sourcePsi?.javaClass})")
    }

    private fun acceptableSourcePsi(sourcePsi: PsiElement?): Boolean {
        if (sourcePsi == null) return true
        if (sourcePsi is LeafPsiElement) return true
        if (sourcePsi is KtElement && sourcePsi.firstChild == null) return true
        return false
    }

    override val uastParent: UElement? by lazy {
        if (givenParent != null) return@lazy givenParent
        val parent = sourcePsi?.parent ?: return@lazy null
        getIdentifierParentForCall(parent) ?: parent.toUElement()
    }

    private fun getIdentifierParentForCall(parent: PsiElement): UElement? {
        val parentParent = parent.parent
        if (parentParent is KtCallElement && parentParent.calleeExpression == parent) { // method identifiers in calls
            return parentParent.toUElement()
        }
        (parent.parents().take(3).find { it is KtTypeReference && it.parent is KtConstructorCalleeExpression })?.let {
            return it.parent.parent.toUElement()
        }
        return null
    }

    constructor(javaPsi: PsiElement?, sourcePsi: PsiElement?, uastParent: UElement?) : this(javaPsi, sourcePsi, javaPsi, uastParent)
    constructor(sourcePsi: PsiElement?, uastParent: UElement?) : this(null, sourcePsi, sourcePsi, uastParent)
}