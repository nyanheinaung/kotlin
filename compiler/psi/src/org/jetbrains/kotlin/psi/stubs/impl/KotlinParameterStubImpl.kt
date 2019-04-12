/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.stubs.KotlinParameterStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.name.FqName
import com.intellij.psi.PsiElement

class KotlinParameterStubImpl(
    parent: StubElement<out PsiElement>?,
    private val fqName: StringRef?,
    private val name: StringRef?,
    private val isMutable: Boolean,
    private val hasValOrVar: Boolean,
    private val hasDefaultValue: Boolean
) : KotlinStubBaseImpl<KtParameter>(parent, KtStubElementTypes.VALUE_PARAMETER), KotlinParameterStub {
    override fun getName(): String? {
        return StringRef.toString(name)
    }

    override fun getFqName(): FqName? {
        return if (fqName != null) FqName(fqName.string) else null
    }

    override fun isMutable() = isMutable
    override fun hasValOrVar() = hasValOrVar
    override fun hasDefaultValue() = hasDefaultValue
}
