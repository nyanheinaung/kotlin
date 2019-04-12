/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor.fixers

import com.intellij.lang.SmartEnterProcessorWithFixers
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.editor.KotlinSmartEnterHandler
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class KotlinCatchParameterFixer : SmartEnterProcessorWithFixers.Fixer<KotlinSmartEnterHandler>() {
    override fun apply(editor: Editor, processor: KotlinSmartEnterHandler, psiElement: PsiElement) {
        if (psiElement !is KtCatchClause) return

        val catchEnd = psiElement.node.findChildByType(KtTokens.CATCH_KEYWORD)!!.textRange!!.endOffset

        val parameterList = psiElement.parameterList
        if (parameterList == null || parameterList.node.findChildByType(KtTokens.RPAR) == null) {
            val endOffset = Math.min(psiElement.endOffset, psiElement.catchBody?.startOffset ?: Int.MAX_VALUE)
            val parameter = parameterList?.parameters?.firstOrNull()?.text ?: ""
            editor.document.replaceString(catchEnd, endOffset, "($parameter)")
            processor.registerUnresolvedError(endOffset - 1)
        } else if (parameterList.parameters.firstOrNull()?.text.isNullOrBlank()) {
            processor.registerUnresolvedError(parameterList.startOffset + 1)
        }
    }
}