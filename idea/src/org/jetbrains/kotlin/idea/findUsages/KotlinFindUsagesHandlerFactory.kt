/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.findUsages

import com.intellij.CommonBundle
import com.intellij.find.FindBundle
import com.intellij.find.findUsages.FindUsagesHandler
import com.intellij.find.findUsages.FindUsagesHandlerFactory
import com.intellij.find.findUsages.FindUsagesOptions
import com.intellij.find.findUsages.JavaFindUsagesHandlerFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.OverridingMethodsSearch
import org.jetbrains.kotlin.asJava.toLightMethods
import org.jetbrains.kotlin.asJava.unwrapped
import org.jetbrains.kotlin.idea.core.isOverridable
import org.jetbrains.kotlin.idea.findUsages.handlers.DelegatingFindMemberUsagesHandler
import org.jetbrains.kotlin.idea.findUsages.handlers.KotlinFindClassUsagesHandler
import org.jetbrains.kotlin.idea.findUsages.handlers.KotlinFindMemberUsagesHandler
import org.jetbrains.kotlin.idea.findUsages.handlers.KotlinTypeParameterFindUsagesHandler
import org.jetbrains.kotlin.idea.refactoring.checkSuperMethods
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.parameterIndex

class KotlinFindUsagesHandlerFactory(project: Project) : FindUsagesHandlerFactory() {
    val javaHandlerFactory = JavaFindUsagesHandlerFactory(project)

    val findFunctionOptions: KotlinFunctionFindUsagesOptions = KotlinFunctionFindUsagesOptions(project)
    val findPropertyOptions = KotlinPropertyFindUsagesOptions(project)
    val findClassOptions = KotlinClassFindUsagesOptions(project)
    val defaultOptions = FindUsagesOptions(project)

    override fun canFindUsages(element: PsiElement): Boolean =
        element is KtClassOrObject ||
                element is KtNamedFunction ||
                element is KtProperty ||
                element is KtParameter ||
                element is KtTypeParameter ||
                element is KtConstructor<*>

    override fun createFindUsagesHandler(element: PsiElement, forHighlightUsages: Boolean): FindUsagesHandler {
        when (element) {
            is KtClassOrObject ->
                return KotlinFindClassUsagesHandler(element, this)

            is KtParameter -> {
                if (!forHighlightUsages) {
                    if (element.hasValOrVar()) {
                        val declarationsToSearch = checkSuperMethods(element, null, "find usages of")
                        return handlerForMultiple(element, declarationsToSearch)
                    }
                    val function = element.ownerFunction
                    if (function != null && function.isOverridable()) {
                        val psiMethod = function.toLightMethods().singleOrNull()
                        if (psiMethod != null) {
                            val hasOverridden = OverridingMethodsSearch.search(psiMethod).any()
                            if (hasOverridden && askWhetherShouldSearchForParameterInOverridingMethods(element)) {
                                val parametersCount = psiMethod.parameterList.parametersCount
                                val parameterIndex = element.parameterIndex()
                                assert(parameterIndex < parametersCount)
                                val overridingParameters = OverridingMethodsSearch.search(psiMethod, true)
                                    .filter { it.parameterList.parametersCount == parametersCount }
                                    .mapNotNull { it.parameterList.parameters[parameterIndex].unwrapped }
                                return handlerForMultiple(element, listOf(element) + overridingParameters)
                            }
                        }

                    }
                }

                return KotlinFindMemberUsagesHandler.getInstance(element, factory = this)
            }

            is KtNamedFunction, is KtProperty, is KtConstructor<*> -> {
                val declaration = element as KtNamedDeclaration

                if (forHighlightUsages) {
                    return KotlinFindMemberUsagesHandler.getInstance(declaration, factory = this)
                }

                val declarationsToSearch = checkSuperMethods(declaration, null, "find usages of")
                return handlerForMultiple(declaration, declarationsToSearch)
            }

            is KtTypeParameter ->
                return KotlinTypeParameterFindUsagesHandler(element, this)

            else ->
                throw IllegalArgumentException("unexpected element type: $element")
        }
    }

    private fun handlerForMultiple(originalDeclaration: KtNamedDeclaration, declarations: Collection<PsiElement>): FindUsagesHandler {
        return when (declarations.size) {
            0 -> FindUsagesHandler.NULL_HANDLER

            1 -> {
                val target = declarations.single().unwrapped ?: return FindUsagesHandler.NULL_HANDLER
                if (target is KtNamedDeclaration) {
                    KotlinFindMemberUsagesHandler.getInstance(target, factory = this)
                } else {
                    javaHandlerFactory.createFindUsagesHandler(target, false)!!
                }
            }

            else -> DelegatingFindMemberUsagesHandler(originalDeclaration, declarations, factory = this)
        }
    }

    private fun askWhetherShouldSearchForParameterInOverridingMethods(parameter: KtParameter): Boolean {
        return Messages.showOkCancelDialog(
            parameter.project,
            FindBundle.message("find.parameter.usages.in.overriding.methods.prompt", parameter.name),
            FindBundle.message("find.parameter.usages.in.overriding.methods.title"),
            CommonBundle.getYesButtonText(), CommonBundle.getNoButtonText(),
            Messages.getQuestionIcon()
        ) == Messages.OK
    }
}
