/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.IrElementBase
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBody
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBodyKind
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrSyntheticBodyImpl(
    startOffset: Int,
    endOffset: Int,
    override val kind: IrSyntheticBodyKind
) :
    IrElementBase(startOffset, endOffset),
    IrSyntheticBody {
    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitSyntheticBody(this, data)
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        // no children
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        // no children
    }

    override fun toString(): String =
        "IrSyntheticBodyImpl($kind)"
}