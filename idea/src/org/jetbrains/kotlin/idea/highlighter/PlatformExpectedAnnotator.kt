/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analyzer.common.CommonPlatform
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.idea.caches.project.implementingDescriptors
import org.jetbrains.kotlin.idea.caches.resolve.findModuleDescriptor
import org.jetbrains.kotlin.idea.core.toDescriptor
import org.jetbrains.kotlin.idea.project.TargetPlatformDetector
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.hasExpectModifier
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.checkers.ExpectedActualDeclarationChecker
import org.jetbrains.kotlin.resolve.diagnostics.SimpleDiagnostics
import org.jetbrains.kotlin.resolve.jvm.multiplatform.JavaActualAnnotationArgumentExtractor

class PlatformExpectedAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val declaration = element as? KtNamedDeclaration ?: return
        if (!isExpectedDeclaration(declaration)) return

        if (TargetPlatformDetector.getPlatform(declaration.containingKtFile) !is CommonPlatform) return

        val implementingModules = declaration.findModuleDescriptor().implementingDescriptors
        if (implementingModules.isEmpty()) return

        val descriptor = declaration.toDescriptor() as? MemberDescriptor ?: return
        if (!descriptor.isExpect) return

        // TODO: obtain the list of annotation argument extractors from platform somehow
        val checker = ExpectedActualDeclarationChecker(listOf(JavaActualAnnotationArgumentExtractor()))

        val trace = BindingTraceContext()
        for (module in implementingModules) {
            checker.checkExpectedDeclarationHasActual(declaration, descriptor, trace, module, ExpectActualTracker.DoNothing)
        }

        val suppressionCache = KotlinCacheService.getInstance(declaration.project).getSuppressionCache()
        val filteredList = trace.bindingContext.diagnostics.filter { diagnostic ->
            !suppressionCache.isSuppressed(declaration, diagnostic.factory.name, diagnostic.severity)
        }
        if (filteredList.isEmpty()) return

        KotlinPsiChecker().annotateElement(declaration, holder, SimpleDiagnostics(filteredList))
    }

    private fun isExpectedDeclaration(declaration: KtDeclaration): Boolean {
        return declaration.hasExpectModifier() ||
                declaration is KtClassOrObject && KtPsiUtil.getOutermostClassOrObject(declaration)?.hasExpectModifier() == true
    }
}
