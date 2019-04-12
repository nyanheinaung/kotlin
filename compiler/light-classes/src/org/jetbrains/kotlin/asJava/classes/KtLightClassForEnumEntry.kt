/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.classes

import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiEnumConstantInitializer
import org.jetbrains.kotlin.psi.KtEnumEntry

internal class KtLightClassForEnumEntry(
        enumEntry: KtEnumEntry,
        private val enumConstant: PsiEnumConstant
): KtLightClassForAnonymousDeclaration(enumEntry), PsiEnumConstantInitializer {
    override fun getEnumConstant(): PsiEnumConstant = enumConstant
    override fun copy() = KtLightClassForEnumEntry(classOrObject as KtEnumEntry, enumConstant)

    override fun getParent() = enumConstant
}
