/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class KtSecondaryConstructor : KtConstructor<KtSecondaryConstructor> {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinPlaceHolderStub<KtSecondaryConstructor>) : super(stub, KtStubElementTypes.SECONDARY_CONSTRUCTOR)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D) = visitor.visitSecondaryConstructor(this, data)

    override fun getContainingClassOrObject() = parent.parent as KtClassOrObject

    override fun getBodyExpression() = findChildByClass(KtBlockExpression::class.java)

    override fun getConstructorKeyword() = notNullChild<PsiElement>(super.getConstructorKeyword())

    fun getDelegationCall(): KtConstructorDelegationCall = findNotNullChildByClass(KtConstructorDelegationCall::class.java)

    fun hasImplicitDelegationCall(): Boolean = getDelegationCall().isImplicit

    fun replaceImplicitDelegationCallWithExplicit(isThis: Boolean): KtConstructorDelegationCall {
        val psiFactory = KtPsiFactory(project)
        val current = getDelegationCall()

        assert(current.isImplicit) { "Method should not be called with explicit delegation call: " + text }
        current.delete()

        val colon = addAfter(psiFactory.createColon(), valueParameterList)

        val delegationName = if (isThis) "this" else "super"

        return addAfter(psiFactory.creareDelegatedSuperTypeEntry(delegationName + "()"), colon) as KtConstructorDelegationCall
    }
}
