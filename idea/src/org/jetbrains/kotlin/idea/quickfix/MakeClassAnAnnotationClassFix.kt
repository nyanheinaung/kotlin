/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.refactoring.canRefactor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

class MakeClassAnAnnotationClassFix(annotationClass: KtClass) : KotlinQuickFixAction<KtClass>(annotationClass) {
    override fun getFamilyName() = "Make class an annotation class"

    override fun getText() = element?.let { "Make '${it.name}' an annotation class" } ?: ""

    public override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        element.addModifier(KtTokens.ANNOTATION_KEYWORD)
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): IntentionAction? {
            val typeReference = diagnostic.psiElement.getNonStrictParentOfType<KtAnnotationEntry>()?.typeReference ?: return null
            val klass = typeReference.classForRefactor() ?: return null
            return MakeClassAnAnnotationClassFix(klass)
        }
    }
}