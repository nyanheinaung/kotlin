/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.builders

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns

interface IrGenerator {
    val context: IrGeneratorContext
}

interface IrGeneratorWithScope : IrGenerator {
    val scope: Scope
}

abstract class IrGeneratorContext {
    abstract val irBuiltIns: IrBuiltIns

    val builtIns: KotlinBuiltIns get() = irBuiltIns.builtIns
}

open class IrGeneratorContextBase(override val irBuiltIns: IrBuiltIns) : IrGeneratorContext()