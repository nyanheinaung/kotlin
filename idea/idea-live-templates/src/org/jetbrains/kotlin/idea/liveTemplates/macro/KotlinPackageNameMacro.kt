/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.liveTemplates.macro

import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.ExpressionContext
import com.intellij.codeInsight.template.Result
import com.intellij.codeInsight.template.TextResult
import org.jetbrains.kotlin.psi.KtFile

class KotlinPackageNameMacro : KotlinMacro() {
    override fun getName() = "kotlinPackageName"
    override fun getPresentableName() = "kotlinPackageName()"

    override fun calculateResult(params: Array<Expression>, context: ExpressionContext): Result? {
        val file = context.psiElementAtStartOffset?.containingFile as? KtFile ?: return null
        val packageName = file.packageFqName.asString().takeIf { it.isNotEmpty() } ?: return null
        return TextResult(packageName)
    }
}