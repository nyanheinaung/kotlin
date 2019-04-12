/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.optimizer

import org.junit.Test

class RedundantStatementEliminationTest() : BasicOptimizerTest("redundant-statement-elimination") {
    @Test fun binary() = box()

    @Test fun unary() = box()

    @Test fun conditional() = box()

    @Test fun literal() = box()

    @Test fun parameters() = box()

    @Test fun comma() = box()
}
