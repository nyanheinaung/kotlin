/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.move.changePackage

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.template.*
import com.intellij.codeInsight.template.impl.TemplateState
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import org.jetbrains.kotlin.idea.codeInsight.CodeInsightUtils
import org.jetbrains.kotlin.idea.intentions.SelfTargetingOffsetIndependentIntention
import org.jetbrains.kotlin.idea.refactoring.hasIdentifiersOnly
import org.jetbrains.kotlin.idea.core.quoteSegmentsIfNeeded
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.FqNameUnsafe
import org.jetbrains.kotlin.psi.KtPackageDirective

class ChangePackageIntention: SelfTargetingOffsetIndependentIntention<KtPackageDirective>(KtPackageDirective::class.java, "Change package") {
    companion object {
        private val PACKAGE_NAME_VAR = "PACKAGE_NAME"
    }

    override fun isApplicableTo(element: KtPackageDirective) = element.packageNameExpression != null

    override fun applyTo(element: KtPackageDirective, editor: Editor?) {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            throw UnsupportedOperationException("Do not call applyTo() in the test mode")
        }

        if (editor == null) throw IllegalArgumentException("This intention requires an editor")

        val file = element.containingKtFile
        val project = file.project

        val nameExpression = element.packageNameExpression!!
        val currentName = element.qualifiedName

        val builder = TemplateBuilderImpl(file)
        builder.replaceElement(
                nameExpression,
                PACKAGE_NAME_VAR,
                object: Expression() {
                    override fun calculateQuickResult(context: ExpressionContext?) = TextResult(currentName)
                    override fun calculateResult(context: ExpressionContext?) = TextResult(currentName)
                    override fun calculateLookupItems(context: ExpressionContext?) = arrayOf(LookupElementBuilder.create(currentName))
                },
                true
        )

        var enteredName: String? = null
        var affectedRange: TextRange? = null

        editor.caretModel.moveToOffset(0)
        TemplateManager.getInstance(project).startTemplate(
                editor,
                builder.buildInlineTemplate(),
                object: TemplateEditingAdapter() {
                    override fun beforeTemplateFinished(state: TemplateState, template: Template?) {
                        if (state == null) return
                        enteredName = state.getVariableValue(PACKAGE_NAME_VAR)!!.text
                        affectedRange = state.getSegmentRange(0)
                    }

                    override fun templateFinished(template: Template, brokenOff: Boolean) {
                        if (brokenOff || enteredName == null || affectedRange == null) return

                        // Restore original name and run refactoring

                        val document = editor.document
                        project.executeWriteCommand(text) {
                            document.replaceString(affectedRange!!.startOffset, affectedRange!!.endOffset, FqName(currentName).quoteSegmentsIfNeeded())
                        }
                        PsiDocumentManager.getInstance(project).commitDocument(document)
                        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)

                        if (!FqNameUnsafe(enteredName!!).hasIdentifiersOnly()) {
                            CodeInsightUtils.showErrorHint(project, editor, "$enteredName is not a valid package name", "Change package", null)
                            return
                        }

                        KotlinChangePackageRefactoring(file).run(FqName(enteredName!!))
                    }
                }
        )
    }
}
