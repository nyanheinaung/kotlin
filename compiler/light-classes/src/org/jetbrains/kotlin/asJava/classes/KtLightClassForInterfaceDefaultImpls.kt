/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.classes

import com.intellij.psi.*
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.asJava.builder.LightClassData
import org.jetbrains.kotlin.load.java.JvmAbi
import org.jetbrains.kotlin.psi.KtClassOrObject

class KtLightClassForInterfaceDefaultImpls(classOrObject: KtClassOrObject)
    : KtLightClassForSourceDeclaration(classOrObject) {
    override fun getQualifiedName(): String? = containingClass?.qualifiedName?.let { it + ".${JvmAbi.DEFAULT_IMPLS_CLASS_NAME}" }

    override fun getName() = JvmAbi.DEFAULT_IMPLS_CLASS_NAME
    override fun getParent() = containingClass

    override fun copy(): PsiElement {
        return KtLightClassForInterfaceDefaultImpls(classOrObject.copy() as KtClassOrObject)
    }

    override fun findLightClassData(): LightClassData {
        return getLightClassDataHolder().findDataForDefaultImpls(classOrObject)
    }

    override fun getTypeParameterList(): PsiTypeParameterList? = null
    override fun getTypeParameters(): Array<PsiTypeParameter> = emptyArray()

    override fun computeModifiers() = publicStaticFinal

    override fun isInterface(): Boolean = false
    override fun isDeprecated(): Boolean = false
    override fun isAnnotationType(): Boolean = false
    override fun isEnum(): Boolean = false
    override fun hasTypeParameters(): Boolean = false
    override fun isInheritor(baseClass: PsiClass, checkDeep: Boolean): Boolean = false

    @Throws(IncorrectOperationException::class)
    override fun setName(name: String): PsiElement {
        throw IncorrectOperationException("Impossible to rename DefaultImpls")
    }

    override fun getContainingClass() = KtLightClassForSourceDeclaration.create(classOrObject)

    override fun getOwnInnerClasses() = emptyList<PsiClass>()
}

private val publicStaticFinal = setOf(PsiModifier.PUBLIC, PsiModifier.STATIC, PsiModifier.FINAL)