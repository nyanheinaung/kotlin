/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import org.jetbrains.kotlin.psi.KtElementImpl
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

class KDocLink(node: ASTNode) : KtElementImpl(node) {
    fun getLinkText(): String = getLinkTextRange().substring(text)

    fun getLinkTextRange(): TextRange {
        val text = text
        if (text.startsWith('[') && text.endsWith(']')) {
            return TextRange(1, text.length - 1)
        }
        return TextRange(0, text.length)
    }

    /**
     * If this link is the subject of a tag, returns the tag. Otherwise, returns null.
     */
    fun getTagIfSubject(): KDocTag? {
        val tag = getStrictParentOfType<KDocTag>()
        return if (tag != null && tag.getSubjectLink() == this) tag else null
    }

    override fun getReferences(): Array<out PsiReference> =
        ReferenceProvidersRegistry.getReferencesFromProviders(this)
}
