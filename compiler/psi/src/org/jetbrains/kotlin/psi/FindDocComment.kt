/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.findDocComment

import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.psiUtil.allChildren

fun findDocComment(declaration: KtDeclaration): KDoc? {
    return declaration.allChildren
        .flatMap {
            if (it is KtDeclarationModifierList) {
                return@flatMap it.children.asSequence()
            }
            sequenceOf(it)
        }
        .dropWhile { it !is KDoc }
        .firstOrNull() as? KDoc
}
