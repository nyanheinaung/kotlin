/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.codeInsight.daemon.ChangeLocalityDetector
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.parents

class KotlinChangeLocalityDetector : ChangeLocalityDetector {
    override fun getChangeHighlightingDirtyScopeFor(element: PsiElement): PsiElement? {
        val parent = element.parent
        if (element is KtBlockExpression && parent is KtNamedFunction && parent.name != null) {
            if (parent.parents.all { it is KtClassBody || it is KtClassOrObject || it is KtFile || it is KtScript }) {
                return parent
            }
        }

        return null
    }
}