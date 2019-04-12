/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.kdoc

import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.kdoc.psi.impl.KDocName

class KDocUnresolvedReferenceInspection(): AbstractKotlinInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        KDocUnresolvedReferenceVisitor(holder)

    private class KDocUnresolvedReferenceVisitor(private val holder: ProblemsHolder): PsiElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is KDocName) {
                val ref = element.mainReference
                if (ref.resolve() == null) {
                    holder.registerProblem(ref)
                }
            }
        }
    }
}
