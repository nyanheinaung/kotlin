/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.Name

interface IrModuleFragment : IrElement {
    val name: Name
    val descriptor: ModuleDescriptor
    val irBuiltins: IrBuiltIns
    val files: MutableList<IrFile>

    override val startOffset: Int get() = UNDEFINED_OFFSET
    override val endOffset: Int get() = UNDEFINED_OFFSET

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrElement =
        accept(transformer, data) as IrModuleFragment
}

