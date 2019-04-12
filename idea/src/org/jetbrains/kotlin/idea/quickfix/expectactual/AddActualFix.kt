/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix.expectactual

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.createSmartPointer
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils

class AddActualFix(
    actualClassOrObject: KtClassOrObject,
    expectedClassOrObject: KtClassOrObject,
    missedDeclarations: List<KtDeclaration>
) : KotlinQuickFixAction<KtClassOrObject>(actualClassOrObject) {

    private val expectedClassPointer = expectedClassOrObject.createSmartPointer()

    private val missedDeclarationPointers = missedDeclarations.map { it.createSmartPointer() }

    override fun getFamilyName() = text

    override fun getText() = "Add missing actual members"

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        val expectedClass = expectedClassPointer.element ?: return
        val missedDeclarations = missedDeclarationPointers.mapNotNull { it.element }
        if (missedDeclarations.isEmpty()) return
        val factory = KtPsiFactory(element)
        val pureActualClass = factory.generateClassOrObject(
            project, false, expectedClass,
            missedDeclarations = missedDeclarations
        )

        fun PsiElement.clean() {
            val reformatted = CodeStyleManager.getInstance(project).reformat(this)
            ShortenReferences.DEFAULT.process(reformatted as KtElement)
        }

        for (declaration in pureActualClass.declarations) {
            element.addDeclaration(declaration).clean()
        }
        val primaryConstructor = pureActualClass.primaryConstructor
        if (element.primaryConstructor == null && primaryConstructor != null) {
            element.addAfter(primaryConstructor, element.nameIdentifier).clean()
        }
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): IntentionAction? {
            val incompatibleMap = DiagnosticFactory.cast(diagnostic, Errors.NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS).b

            val expectedClassDescriptor = incompatibleMap.firstOrNull()?.first?.containingDeclaration as? ClassDescriptor
                ?: return null
            val expectedClassOrObject = DescriptorToSourceUtils.descriptorToDeclaration(expectedClassDescriptor) as? KtClassOrObject
                ?: return null

            val missedDeclarations = incompatibleMap.mapNotNull {
                DescriptorToSourceUtils.descriptorToDeclaration(it.first) as? KtDeclaration
            }
            if (missedDeclarations.isEmpty()) return null

            return (diagnostic.psiElement as? KtClassOrObject)?.let {
                AddActualFix(it, expectedClassOrObject, missedDeclarations)
            }
        }
    }
}