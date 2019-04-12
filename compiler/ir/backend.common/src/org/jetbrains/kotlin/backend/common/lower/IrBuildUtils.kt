/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.lower

import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.symbols.IrSymbol

abstract class SymbolWithIrBuilder<out S: IrSymbol, out D: IrDeclaration> {

    protected abstract fun buildSymbol(): S

    protected open fun doInitialize() { }

    protected abstract fun buildIr(): D

    val symbol by lazy { buildSymbol() }

    private val builtIr by lazy { buildIr() }
    private var initialized: Boolean = false

    fun initialize() {
        doInitialize()
        initialized = true
    }

    val ir: D
        get() {
            if (!initialized)
                throw Error("Access to IR before initialization")
            return builtIr
        }
}
