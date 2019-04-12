/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.psi.impl

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

/**
 * The part of a doc comment which describes a single class, method or property
 * produced by the element being documented. For example, the doc comment of a class
 * can have sections for the class itself, its primary constructor and each of the
 * properties defined in the primary constructor.
 */
class KDocSection(node: ASTNode) : KDocTag(node) {
    /**
     * Returns the name of the section (the name of the doc tag introducing the section,
     * or null for the default section).
     */
    override fun getName(): String? =
        (firstChild as? KDocTag)?.name

    override fun getSubjectName(): String? =
        (firstChild as? KDocTag)?.getSubjectName()

    override fun getContent(): String =
        (firstChild as? KDocTag)?.getContent() ?: super.getContent()

    fun findTagsByName(name: String): List<KDocTag> {
        return getChildrenOfType<KDocTag>().filter { it.name == name }
    }

    fun findTagByName(name: String): KDocTag? = findTagsByName(name).firstOrNull()
}
