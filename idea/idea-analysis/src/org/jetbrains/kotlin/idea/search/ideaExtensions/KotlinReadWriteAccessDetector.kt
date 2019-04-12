/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.search.ideaExtensions

import com.intellij.codeInsight.highlighting.JavaReadWriteAccessDetector
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.kotlin.asJava.elements.KtLightMethod
import org.jetbrains.kotlin.idea.references.ReferenceAccess
import org.jetbrains.kotlin.idea.references.readWriteAccess
import org.jetbrains.kotlin.load.java.JvmAbi
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class KotlinReadWriteAccessDetector : ReadWriteAccessDetector() {
    companion object {
        val INSTANCE = KotlinReadWriteAccessDetector()
    }

    override fun isReadWriteAccessible(element: PsiElement) = element is KtVariableDeclaration || element is KtParameter

    override fun isDeclarationWriteAccess(element: PsiElement) = isReadWriteAccessible(element)

    override fun getReferenceAccess(referencedElement: PsiElement, reference: PsiReference): ReadWriteAccessDetector.Access {
        if (!isReadWriteAccessible(referencedElement)) {
            return ReadWriteAccessDetector.Access.Read
        }

        val refTarget = reference.resolve()
        if (refTarget is KtLightMethod) {
            val origin = refTarget.kotlinOrigin
            val declaration: KtNamedDeclaration = when (origin) {
                is KtPropertyAccessor -> origin.getNonStrictParentOfType<KtProperty>()
                is KtProperty, is KtParameter -> origin as KtNamedDeclaration
                else -> null
            } ?: return ReadWriteAccessDetector.Access.ReadWrite

            return when (refTarget.name) {
                JvmAbi.getterName(declaration.name!!) -> return ReadWriteAccessDetector.Access.Read
                JvmAbi.setterName(declaration.name!!) -> return ReadWriteAccessDetector.Access.Write
                else -> ReadWriteAccessDetector.Access.ReadWrite
            }
        }

        return getExpressionAccess(reference.element)
    }

    override fun getExpressionAccess(expression: PsiElement): ReadWriteAccessDetector.Access {
        if (expression !is KtExpression) { //TODO: there should be a more correct scheme of access type detection for cross-language references
            return JavaReadWriteAccessDetector().getExpressionAccess(expression)
        }

        return when (expression.readWriteAccess(useResolveForReadWrite = true)) {
            ReferenceAccess.READ -> ReadWriteAccessDetector.Access.Read
            ReferenceAccess.WRITE -> ReadWriteAccessDetector.Access.Write
            ReferenceAccess.READ_WRITE -> ReadWriteAccessDetector.Access.ReadWrite
        }
    }
}