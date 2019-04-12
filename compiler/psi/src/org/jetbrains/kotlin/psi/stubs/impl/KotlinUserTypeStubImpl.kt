/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.stubs.KotlinUserTypeStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class KotlinUserTypeStubImpl(
    parent: StubElement<out PsiElement>?
) : KotlinStubBaseImpl<KtUserType>(parent, KtStubElementTypes.USER_TYPE), KotlinUserTypeStub
