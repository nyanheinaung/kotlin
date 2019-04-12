/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement

class BlockScope(private val parentScope: BlockScope?, val block: KtElement) {
    val depth: Int = (parentScope?.depth ?: 0) + 1

    val blockScopeForContainingDeclaration: BlockScope? by lazy {
        var scope: BlockScope? = this
        while (scope != null) {
            if (scope.block is KtDeclaration) {
                break
            }
            scope = scope.parentScope
        }
        scope
    }
}
