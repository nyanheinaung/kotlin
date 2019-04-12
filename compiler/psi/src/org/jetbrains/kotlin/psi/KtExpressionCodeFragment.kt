/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes

class KtExpressionCodeFragment(
    project: Project,
    name: String,
    text: CharSequence,
    imports: String?,
    context: PsiElement?
) : KtCodeFragment(project, name, text, imports, KtNodeTypes.EXPRESSION_CODE_FRAGMENT, context) {

    override fun getContentElement() = findChildByClass(KtExpression::class.java)
}
