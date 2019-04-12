/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.codeInsight.daemon.ProblemHighlightFilter
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.KotlinFileType

class KotlinProblemHighlightFilter : ProblemHighlightFilter() {

    override fun shouldHighlight(psiFile: PsiFile): Boolean {
        if (psiFile.fileType == KotlinFileType.INSTANCE && !KotlinHighlightingUtil.shouldHighlight(psiFile)) return false

        return true
    }
}
