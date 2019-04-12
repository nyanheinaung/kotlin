/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.references

import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceRegistrar
import org.jetbrains.kotlin.idea.kdoc.KDocReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.parents

class KotlinReferenceContributor() : AbstractKotlinReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        with(registrar) {
            registerProvider(factory = ::KtSimpleNameReference)

            registerMultiProvider<KtNameReferenceExpression> {
                if (it.getReferencedNameElementType() != KtTokens.IDENTIFIER) return@registerMultiProvider emptyArray()
                if (it.parents.any { it is KtImportDirective || it is KtPackageDirective || it is KtUserType }) {
                    return@registerMultiProvider emptyArray()
                }

                when (it.readWriteAccess(useResolveForReadWrite = false)) {
                    ReferenceAccess.READ ->
                        arrayOf<PsiReference>(SyntheticPropertyAccessorReference.Getter(it))
                    ReferenceAccess.WRITE ->
                        arrayOf<PsiReference>(SyntheticPropertyAccessorReference.Setter(it))
                    ReferenceAccess.READ_WRITE ->
                        arrayOf<PsiReference>(SyntheticPropertyAccessorReference.Getter(it), SyntheticPropertyAccessorReference.Setter(it))
                }
            }

            registerProvider(factory = ::KtConstructorDelegationReference)

            registerProvider(factory = ::KtInvokeFunctionReference)

            registerProvider(factory = ::KtArrayAccessReference)

            registerProvider(factory = ::KtCollectionLiteralReference)

            registerProvider(factory = ::KtForLoopInReference)

            registerProvider(factory = ::KtPropertyDelegationMethodsReference)

            registerProvider(factory = ::KtDestructuringDeclarationReference)

            registerProvider(factory = ::KDocReference)
        }
    }
}
