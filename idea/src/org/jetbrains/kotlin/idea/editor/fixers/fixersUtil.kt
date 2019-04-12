/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor.fixers

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

val PsiElement.range: TextRange get() = textRange!!
val TextRange.start: Int get() = startOffset
val TextRange.end: Int get() = endOffset

fun PsiElement.startLine(doc: Document): Int = doc.getLineNumber(range.start)
fun PsiElement.endLine(doc: Document): Int = doc.getLineNumber(range.end)
fun PsiElement?.isWithCaret(caret: Int) = this?.textRange?.contains(caret) == true
