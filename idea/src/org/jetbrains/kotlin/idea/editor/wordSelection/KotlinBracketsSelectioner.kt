/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor.wordSelection

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandlerBase
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtContainerNode

class KotlinBracketsSelectioner : ExtendWordSelectionHandlerBase() {
    override fun canSelect(e: PsiElement): Boolean {
        return e is KtContainerNode && e.node.elementType == KtNodeTypes.INDICES
    }
}