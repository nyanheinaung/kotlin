/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg

import org.jetbrains.kotlin.psi.KtElement

import java.util.Collections

abstract class BreakableBlockInfo(open val element: KtElement, val entryPoint: Label, val exitPoint: Label) : BlockInfo() {
    val referablePoints: MutableSet<Label> = hashSetOf()

    init {
        markReferablePoints(entryPoint, exitPoint)
    }

    protected fun markReferablePoints(vararg labels: Label) {
        Collections.addAll(referablePoints, *labels)
    }
}
