/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor.fixers

import com.intellij.lang.SmartEnterProcessorWithFixers
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.editor.KotlinSmartEnterHandler
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getPrevSiblingIgnoringWhitespaceAndComments

class KotlinClassBodyFixer : SmartEnterProcessorWithFixers.Fixer<KotlinSmartEnterHandler>() {
    override fun apply(editor: Editor, processor: KotlinSmartEnterHandler, psiElement: PsiElement) {
        if (psiElement !is KtClassOrObject) return

        val body = psiElement.getBody()
        if (!body?.text.isNullOrBlank()) return

        var endOffset = psiElement.range.end

        if (body != null) {
            body.getPrevSiblingIgnoringWhitespaceAndComments()?.let {
                endOffset = it.endOffset
            }
        }

        editor.caretModel.moveToOffset(endOffset - 1)

        // Insert '\n' to force a multiline body, otherwise there will be an empty body on one line and a caret on the next one.
        editor.document.insertString(endOffset, "{\n}")
    }
}