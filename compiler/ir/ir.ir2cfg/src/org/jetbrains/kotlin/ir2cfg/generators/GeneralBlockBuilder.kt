/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.generators

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir2cfg.builders.BasicBlockBuilder
import org.jetbrains.kotlin.ir2cfg.builders.BlockConnectorBuilder

class GeneralBlockBuilder(override val incoming: BlockConnectorBuilder?) : BasicBlockBuilder {

    private val elements = mutableListOf<IrStatement>()

    override fun add(element: IrStatement) {
        elements.add(element)
    }

    override val last: IrStatement?
        get() = elements.lastOrNull()

    override fun build() = BasicBlockImpl(elements.toList())
}