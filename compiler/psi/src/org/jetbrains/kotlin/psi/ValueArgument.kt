/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.name.Name

interface ValueArgument {
    @IfNotParsed
    fun getArgumentExpression(): KtExpression?

    fun getArgumentName(): ValueArgumentName?

    fun isNamed(): Boolean

    fun asElement(): KtElement

    /* The '*' in something like foo(*arr) i.e. pass an array as a number of vararg arguments */
    fun getSpreadElement(): LeafPsiElement?

    /* The argument is placed externally to call element, e.g. in 'when' condition with subject: 'when (a) { in c -> }' */
    fun isExternal(): Boolean
}

interface LambdaArgument : ValueArgument {
    fun getLambdaExpression(): KtLambdaExpression?
}

interface ValueArgumentName {
    val asName: Name
    val referenceExpression: KtSimpleNameExpression?
}
