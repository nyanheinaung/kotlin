/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.optimizer

import org.junit.Test

class EmptyStatementEliminationTest : BasicOptimizerTest("empty-statement-elimination") {
    @Test fun emptyIfConditionPreserved() = box()

    @Test fun ifWithEmptyThenAndNoElse() = box()

    @Test fun emptyBlockEliminated() = box()

    @Test fun switchElimination() = box()
}
