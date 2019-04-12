/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.stubs.KotlinObjectStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.name.FqName

import com.intellij.psi.PsiElement

class KotlinObjectStubImpl(
    parent: StubElement<out PsiElement>?,
    private val name: StringRef?,
    private val fqName: FqName?,
    private val superNames: Array<StringRef>,
    private val isTopLevel: Boolean,
    private val isDefault: Boolean,
    private val isLocal: Boolean,
    private val isObjectLiteral: Boolean
) : KotlinStubBaseImpl<KtObjectDeclaration>(parent, KtStubElementTypes.OBJECT_DECLARATION), KotlinObjectStub {
    override fun getFqName() = fqName
    override fun getName() = StringRef.toString(name)
    override fun getSuperNames() = superNames.map { it.toString() }
    override fun isTopLevel() = isTopLevel
    override fun isCompanion() = isDefault
    override fun isObjectLiteral() = isObjectLiteral
    override fun isLocal() = isLocal
}
