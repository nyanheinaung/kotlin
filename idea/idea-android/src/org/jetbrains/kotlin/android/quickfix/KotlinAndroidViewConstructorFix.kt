/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors.SUPERTYPE_NOT_INITIALIZED
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

class KotlinAndroidViewConstructorFix(element: KtSuperTypeEntry) : KotlinQuickFixAction<KtSuperTypeEntry>(element) {

    override fun getText() = "Add Android View constructors using '@JvmOverloads'"
    override fun getFamilyName() = text

    override fun isAvailable(project: Project, editor: Editor?, file: KtFile): Boolean {
        return AndroidFacet.getInstance(file) != null
    }

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        val ktClass = element.containingClass() ?: return

        val factory = KtPsiFactory(element)

        val newPrimaryConstructor = factory.createPrimaryConstructor(
            """(
            context: android.content.Context, attrs: android.util.AttributeSet? = null, defStyleAttr: Int = 0
            )""".trimIndent()
        )

        val primaryConstructor = ktClass.createPrimaryConstructorIfAbsent().replaced(newPrimaryConstructor)
        primaryConstructor.valueParameterList?.let { ShortenReferences.DEFAULT.process(it) }
        primaryConstructor.addAnnotation(fqNameAnnotation)

        element.replace(factory.createSuperTypeCallEntry(element.text + "(context, attrs, defStyleAttr)"))
    }

    companion object Factory : KotlinSingleIntentionActionFactory() {

        private val fqNameAnnotation = FqName("kotlin.jvm.JvmOverloads")

        private val requiredConstructorParameterTypes =
            listOf("android.content.Context", "android.util.AttributeSet", "kotlin.Int")

        override fun createAction(diagnostic: Diagnostic): IntentionAction? {
            val superTypeEntry = SUPERTYPE_NOT_INITIALIZED.cast(diagnostic).psiElement

            val ktClass = superTypeEntry.containingClass() ?: return null
            if (ktClass.primaryConstructor != null) return null

            val context = superTypeEntry.analyze()
            val type = superTypeEntry.typeReference?.let { context[BindingContext.TYPE, it] } ?: return null

            if (!type.isAndroidView() && type.supertypes().none { it.isAndroidView() }) return null

            val names = type.constructorParameters() ?: return null
            if (requiredConstructorParameterTypes !in names) return null

            return KotlinAndroidViewConstructorFix(superTypeEntry)
        }

        private fun KotlinType.getFqNameAsString() = constructor.declarationDescriptor?.fqNameUnsafe?.asString()

        private fun KotlinType.isAndroidView() = getFqNameAsString() == "android.view.View"

        private fun KotlinType.constructorParameters(): List<List<String?>>? {
            val classDescriptor = constructor.declarationDescriptor as? ClassDescriptor ?: return null
            return classDescriptor.constructors.map {
                it.valueParameters.map { parameter -> parameter.type.getFqNameAsString() }
            }
        }
    }
}


