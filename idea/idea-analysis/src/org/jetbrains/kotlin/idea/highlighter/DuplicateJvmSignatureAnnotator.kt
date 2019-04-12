/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.asJava.*
import org.jetbrains.kotlin.idea.caches.project.getModuleInfo
import org.jetbrains.kotlin.idea.caches.resolve.*
import org.jetbrains.kotlin.idea.project.TargetPlatformDetector
import org.jetbrains.kotlin.idea.util.ProjectRootsUtil
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform

class DuplicateJvmSignatureAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is KtFile && element !is KtDeclaration) return
        if (!ProjectRootsUtil.isInProjectSource(element)) return

        val file = element.containingFile
        if (file !is KtFile || TargetPlatformDetector.getPlatform(file) !== JvmPlatform) return

        val otherDiagnostics = when (element) {
            is KtDeclaration -> element.analyzeWithContent()
            is KtFile -> element.analyzeWithContent()
            else -> throw AssertionError("DuplicateJvmSignatureAnnotator: should not get here! Element: ${element.text}")
        }.diagnostics

        val moduleScope = element.getModuleInfo().contentScope()
        val diagnostics = getJvmSignatureDiagnostics(element, otherDiagnostics, moduleScope) ?: return

        KotlinPsiChecker().annotateElement(element, holder, diagnostics)
    }
}
