/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.compiler

import com.intellij.codeInsight.ExternalAnnotationsManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*

class MockExternalAnnotationsManager : ExternalAnnotationsManager() {
    override fun chooseAnnotationsPlace(element: PsiElement): AnnotationPlace? = null

    override fun isExternalAnnotationWritable(listOwner: PsiModifierListOwner, annotationFQN: String): Boolean = false
    override fun isExternalAnnotation(annotation: PsiAnnotation): Boolean = false

    override fun findExternalAnnotationsFiles(listOwner: PsiModifierListOwner): List<PsiFile>? = null
    override fun findExternalAnnotation(listOwner: PsiModifierListOwner, annotationFQN: String): PsiAnnotation? = null
    override fun findExternalAnnotations(listOwner: PsiModifierListOwner): Array<out PsiAnnotation>? = null

    override fun annotateExternally(
        listOwner: PsiModifierListOwner,
        annotationFQName: String,
        fromFile: PsiFile,
        value: Array<out PsiNameValuePair>?
    ) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun deannotate(listOwner: PsiModifierListOwner, annotationFQN: String): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun editExternalAnnotation(
        listOwner: PsiModifierListOwner,
        annotationFQN: String,
        value: Array<out PsiNameValuePair>?
    ): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun hasAnnotationRootsForFile(file: VirtualFile): Boolean {
        throw UnsupportedOperationException("not implemented")
    }
}
