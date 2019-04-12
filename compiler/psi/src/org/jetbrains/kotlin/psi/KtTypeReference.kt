/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.collectAnnotationEntriesFromStubOrPsi
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

/**
 * Type reference element.
 * Underlying token is [org.jetbrains.kotlin.KtNodeTypes.TYPE_REFERENCE]
 */
class KtTypeReference : KtModifierListOwnerStub<KotlinPlaceHolderStub<KtTypeReference>>,
    KtAnnotated, KtAnnotationsContainer {

    constructor(node: ASTNode) : super(node)

    constructor(stub: KotlinPlaceHolderStub<KtTypeReference>) : super(stub, KtStubElementTypes.TYPE_REFERENCE)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    val typeElement: KtTypeElement?
        get() = KtStubbedPsiUtil.getStubOrPsiChild(this, KtStubElementTypes.TYPE_ELEMENT_TYPES, KtTypeElement.ARRAY_FACTORY)

    override fun getAnnotations(): List<KtAnnotation> {
        return modifierList?.annotations.orEmpty()
    }

    override fun getAnnotationEntries(): List<KtAnnotationEntry> {
        return modifierList?.annotationEntries.orEmpty()
    }

    fun hasParentheses(): Boolean {
        return findChildByType<PsiElement>(KtTokens.LPAR) != null && findChildByType<PsiElement>(KtTokens.RPAR) != null
    }
}
