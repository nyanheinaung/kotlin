/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.name.Name

open class KtExpressionWithLabel(node: ASTNode) : KtExpressionImpl(node) {

    fun getTargetLabel(): KtSimpleNameExpression? =
        labelQualifier?.findChildByType(KtNodeTypes.LABEL) as? KtSimpleNameExpression

    val labelQualifier: KtContainerNode?
        get() = findChildByType(KtNodeTypes.LABEL_QUALIFIER)

    fun getLabelName(): String? = getTargetLabel()?.getReferencedName()
    fun getLabelNameAsName(): Name? = getTargetLabel()?.getReferencedNameAsName()

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D) = visitor.visitExpressionWithLabel(this, data)
}
