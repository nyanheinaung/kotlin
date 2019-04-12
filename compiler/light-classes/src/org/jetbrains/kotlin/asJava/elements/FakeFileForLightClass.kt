/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.elements

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.java.LanguageLevel
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.compiled.ClsFileImpl
import com.intellij.psi.stubs.PsiClassHolderFileStub
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.classes.KtLightClassForFacade
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile

open class FakeFileForLightClass(
        val ktFile: KtFile,
        private val lightClass: () -> KtLightClass,
        private val stub: () -> PsiClassHolderFileStub<*>,
        private val packageFqName: FqName = ktFile.packageFqName
) : ClsFileImpl(ktFile.viewProvider) {

    override fun getVirtualFile(): VirtualFile =
        ktFile.virtualFile ?: ktFile.originalFile.virtualFile ?: super.getVirtualFile()

    override fun getPackageName() = packageFqName.asString()

    override fun getStub() = stub()

    override fun getClasses() = arrayOf(lightClass())

    override fun getNavigationElement() = ktFile

    override fun accept(visitor: PsiElementVisitor) {
        // Prevent access to compiled PSI
        // TODO: More complex traversal logic may be implemented when necessary
    }

    // this should be equal to current compiler target language level
    override fun getLanguageLevel() = LanguageLevel.JDK_1_6

    override fun hashCode(): Int {
        val thisClass = lightClass()
        if (thisClass is KtLightClassForSourceDeclaration) return ktFile.hashCode()
        return thisClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FakeFileForLightClass) return false
        val thisClass = lightClass()
        val anotherClass = other.lightClass()

        if (thisClass is KtLightClassForSourceDeclaration) {
            return anotherClass is KtLightClassForSourceDeclaration && ktFile == other.ktFile
        }

        return thisClass == anotherClass
    }

    override fun isEquivalentTo(another: PsiElement?) = this == another

    override fun setPackageName(packageName: String) {
        if (lightClass() is KtLightClassForFacade) {
            ktFile.packageDirective?.fqName = FqName(packageName)
        }
        else {
            super.setPackageName(packageName)
        }
    }

    override fun isPhysical() = false
}
