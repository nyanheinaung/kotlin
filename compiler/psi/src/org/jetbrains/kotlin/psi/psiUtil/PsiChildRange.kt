/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.psiUtil

import com.intellij.psi.PsiElement

data class PsiChildRange(val first: PsiElement?, val last: PsiElement?) : Sequence<PsiElement> {
    init {
        if (first == null) {
            assert(last == null)
        } else {
            assert(first.parent == last!!.parent)
        }
    }

    val isEmpty: Boolean
        get() = first == null

    override fun iterator(): Iterator<PsiElement> {
        val sequence = if (first == null) {
            emptySequence<PsiElement>()
        } else {
            val afterLast = last!!.nextSibling
            first.siblings().takeWhile { it != afterLast }
        }
        return sequence.iterator()
    }

    companion object {
        val EMPTY: PsiChildRange = PsiChildRange(null, null)

        fun singleElement(element: PsiElement): PsiChildRange = PsiChildRange(element, element)
    }
}