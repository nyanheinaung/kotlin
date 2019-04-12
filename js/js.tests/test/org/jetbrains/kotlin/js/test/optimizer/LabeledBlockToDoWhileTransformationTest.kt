/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.optimizer

import org.jetbrains.kotlin.js.backend.ast.JsStatement
import org.jetbrains.kotlin.js.inline.clean.LabeledBlockToDoWhileTransformation
import org.junit.Test

class LabeledBlockToDoWhileTransformationTest : BasicOptimizerTest("labeled-block-to-do-while") {
    @Test fun simple() = box()

    override fun process(statement: JsStatement) {
        LabeledBlockToDoWhileTransformation.apply(statement)
    }
}