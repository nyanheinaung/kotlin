/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.intentions.RemoveExplicitTypeIntention
import org.jetbrains.kotlin.idea.util.CommentSaver
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

open class RemovePsiElementSimpleFix(element: PsiElement, private val text: String) : KotlinQuickFixAction<PsiElement>(element) {
    override fun getFamilyName() = "Remove element"

    override fun getText() = text

    public override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        element?.delete()
    }

    object RemoveImportFactory : KotlinSingleIntentionActionFactory() {
        public override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<PsiElement>? {
            val directive = diagnostic.psiElement.getNonStrictParentOfType<KtImportDirective>() ?: return null
            val refText = directive.importedReference?.let { "for '${it.text}'" } ?: ""
            return RemovePsiElementSimpleFix(directive, "Remove conflicting import $refText")
        }
    }

    object RemoveSpreadFactory : KotlinSingleIntentionActionFactory() {
        public override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<PsiElement>? {
            val element = diagnostic.psiElement
            if (element.node.elementType != KtTokens.MUL) return null
            return RemovePsiElementSimpleFix(element, "Remove '*'")
        }
    }

    object RemoveTypeArgumentsFactory : KotlinSingleIntentionActionFactory() {
        public override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<PsiElement>? {
            val element = diagnostic.psiElement.getNonStrictParentOfType<KtTypeArgumentList>() ?: return null
            return RemovePsiElementSimpleFix(element, "Remove type arguments")
        }
    }

    object RemoveTypeParametersFactory : KotlinSingleIntentionActionFactory() {
        public override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<PsiElement>? {
            val element = diagnostic.psiElement.getNonStrictParentOfType<KtTypeParameterList>() ?: return null
            return RemovePsiElementSimpleFix(element, "Remove type parameters")
        }
    }

    object RemoveVariableFactory : KotlinSingleIntentionActionFactory() {
        public override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<PsiElement>? {
            val expression = diagnostic.psiElement.getNonStrictParentOfType<KtProperty>() ?: return null
            if (!RemoveExplicitTypeIntention.redundantTypeSpecification(expression, expression.initializer)) return null
            return object : RemovePsiElementSimpleFix(expression, "Remove variable '${expression.name}'") {
                override fun invoke(project: Project, editor: Editor?, file: KtFile) {
                    val initializer = expression.initializer
                    if (initializer != null && initializer !is KtConstantExpression) {
                        val commentSaver = CommentSaver(expression)
                        val replaced = expression.replace(initializer)
                        commentSaver.restore(replaced)
                    } else {
                        expression.delete()
                    }
                }
            }
        }
    }
}
