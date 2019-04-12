/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter

/**
 * @return string description of declaration, like `Function "describe"`
 */
fun KtNamedDeclaration.describe(): String? = when (this) {
    is KtClass -> "${if (isInterface()) "Interface" else "Class"} \"$name\""
    is KtObjectDeclaration -> "Object \"$name\""
    is KtNamedFunction -> "Function \"$name\""
    is KtSecondaryConstructor -> "Constructor"
    is KtProperty -> "Property \"$name\""
    is KtParameter -> if (this.isPropertyParameter()) "Property \"$name\"" else "Parameter \"$name\""
    is KtTypeParameter -> "Type parameter \"$name\""
    is KtTypeAlias -> "Type alias \"$name\""
    else -> null
}