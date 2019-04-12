/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.stubs.KotlinTypeAliasStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class KotlinTypeAliasStubImpl(
    parent: StubElement<out PsiElement>?,
    private val name: StringRef?,
    private val qualifiedName: StringRef?,
    private val isTopLevel: Boolean
) : KotlinStubBaseImpl<KtTypeAlias>(parent, KtStubElementTypes.TYPEALIAS), KotlinTypeAliasStub {

    override fun getName(): String? =
        StringRef.toString(name)

    override fun getFqName(): FqName? =
        StringRef.toString(qualifiedName)?.let(::FqName)

    override fun isTopLevel(): Boolean = isTopLevel
}
