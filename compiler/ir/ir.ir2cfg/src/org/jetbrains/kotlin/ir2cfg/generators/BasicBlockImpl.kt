/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.generators

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir2cfg.graph.BasicBlock
import org.jetbrains.kotlin.ir2cfg.graph.BlockConnector

class BasicBlockImpl(override val elements: List<IrStatement>) : BasicBlock {

    override var incoming: BlockConnector? = null
        internal set(arg) {
            if (field != null) throw AssertionError("Incoming connector cannot be changed after being set")
            field = arg
        }

    override var outgoing: BlockConnector? = null
        internal set(arg) {
            if (field != null) throw AssertionError("Outgoing connector cannot be changed after being set")
            field = arg
        }
}