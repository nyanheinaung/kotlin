/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.j2k

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiVariable
import org.jetbrains.kotlin.idea.util.compat.dfaCheckNullability
import org.jetbrains.kotlin.idea.util.compat.dfaInferMethodNullability
import org.jetbrains.kotlin.j2k.*
import org.jetbrains.kotlin.j2k.ast.Nullability
import org.jetbrains.kotlin.idea.util.compat.Nullability as IntellijNullability

object IdeaJavaToKotlinServices : JavaToKotlinConverterServices {
    override val referenceSearcher: ReferenceSearcher
        get() = IdeaReferenceSearcher

    override val superMethodsSearcher: SuperMethodsSearcher
        get() = IdeaSuperMethodSearcher

    override val resolverForConverter: ResolverForConverter
        get() = IdeaResolverForConverter

    override val docCommentConverter: DocCommentConverter
        get() = IdeaDocCommentConverter

    override val javaDataFlowAnalyzerFacade: JavaDataFlowAnalyzerFacade
        get() = IdeaJavaDataFlowAnalyzerFacade
}

object IdeaSuperMethodSearcher : SuperMethodsSearcher {
    override fun findDeepestSuperMethods(method: PsiMethod) = method.findDeepestSuperMethods().asList()
}

private object IdeaJavaDataFlowAnalyzerFacade : JavaDataFlowAnalyzerFacade {
    override fun variableNullability(variable: PsiVariable, context: PsiElement): Nullability =
        dfaCheckNullability(variable, context).toJ2KNullability()

    override fun methodNullability(method: PsiMethod): Nullability =
        dfaInferMethodNullability(method).toJ2KNullability()

    private fun IntellijNullability.toJ2KNullability() = when (this) {
        IntellijNullability.UNKNOWN -> Nullability.Default
        IntellijNullability.NOT_NULL -> Nullability.NotNull
        IntellijNullability.NULLABLE -> Nullability.Nullable
    }
}