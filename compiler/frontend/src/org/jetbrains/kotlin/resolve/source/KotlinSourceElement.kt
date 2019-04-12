/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.source

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPureElement

class KotlinSourceElement(override val psi: KtElement) : PsiSourceElement

fun KtPureElement?.toSourceElement(): SourceElement = if (this == null) SourceElement.NO_SOURCE else KotlinSourceElement(psiOrParent)

fun SourceElement.getPsi(): PsiElement? = (this as? PsiSourceElement)?.psi
