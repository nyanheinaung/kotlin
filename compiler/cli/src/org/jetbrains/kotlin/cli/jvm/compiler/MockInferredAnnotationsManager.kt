/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.compiler

import com.intellij.codeInsight.InferredAnnotationsManager
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiModifierListOwner

class MockInferredAnnotationsManager : InferredAnnotationsManager() {
    override fun findInferredAnnotation(listOwner: PsiModifierListOwner, annotationFQN: String): PsiAnnotation? = null
    override fun findInferredAnnotations(listOwner: PsiModifierListOwner): Array<out PsiAnnotation> = EMPTY_PSI_ANNOTATION_ARRAY
    override fun isInferredAnnotation(annotation: PsiAnnotation): Boolean = false

    companion object {
        val EMPTY_PSI_ANNOTATION_ARRAY = arrayOf<PsiAnnotation>()
    }
}