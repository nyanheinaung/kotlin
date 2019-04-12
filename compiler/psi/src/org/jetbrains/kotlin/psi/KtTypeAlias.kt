/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentationProviders
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.KotlinTypeAliasStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class KtTypeAlias : KtTypeParameterListOwnerStub<KotlinTypeAliasStub>, KtNamedDeclaration {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinTypeAliasStub) : super(stub, KtStubElementTypes.TYPEALIAS)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitTypeAlias(this, data)

    fun isTopLevel(): Boolean =
        stub?.isTopLevel() ?: parent is KtFile

    @IfNotParsed
    fun getTypeAliasKeyword(): PsiElement? =
        findChildByType(KtTokens.TYPE_ALIAS_KEYWORD)

    @IfNotParsed
    fun getTypeReference(): KtTypeReference? {
        return if (stub != null) {
            val typeReferences =
                getStubOrPsiChildrenAsList<KtTypeReference, KotlinPlaceHolderStub<KtTypeReference>>(KtStubElementTypes.TYPE_REFERENCE)
            typeReferences[0]
        } else {
            findChildByType(KtNodeTypes.TYPE_REFERENCE)
        }
    }

    override fun getPresentation() = ItemPresentationProviders.getItemPresentation(this)
}
