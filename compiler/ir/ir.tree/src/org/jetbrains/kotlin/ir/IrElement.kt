/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir

import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

interface IrElement {
    val startOffset: Int
    val endOffset: Int

    fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R

    fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D): Unit

    fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrElement =
        accept(transformer, data)

    fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D): Unit
}

interface IrStatement : IrElement {
    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrStatement =
        super.transform(transformer, data) as IrStatement
}

inline fun <reified T : IrElement> IrElement.assertCast(): T =
    if (this is T) this else throw AssertionError("Expected ${T::class.simpleName}: $this")
