/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.stubs.KotlinAnnotationEntryStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import com.intellij.psi.PsiElement

class KotlinAnnotationEntryStubImpl(
    parent: StubElement<out PsiElement>?,
    private val shortName: StringRef?,
    private val hasValueArguments: Boolean
) : KotlinStubBaseImpl<KtAnnotationEntry>(parent, KtStubElementTypes.ANNOTATION_ENTRY), KotlinAnnotationEntryStub {

    override fun getShortName() = shortName?.string

    override fun hasValueArguments() = hasValueArguments
}
