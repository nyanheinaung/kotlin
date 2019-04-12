/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg

import org.jetbrains.kotlin.psi.KtLoopExpression

class LoopInfo(
    override val element: KtLoopExpression,
    entryPoint: Label,
    exitPoint: Label,
    val bodyEntryPoint: Label,
    val bodyExitPoint: Label,
    val conditionEntryPoint: Label
) : BreakableBlockInfo(element, entryPoint, exitPoint) {

    init {
        markReferablePoints(bodyEntryPoint, bodyExitPoint, conditionEntryPoint)
    }
}
