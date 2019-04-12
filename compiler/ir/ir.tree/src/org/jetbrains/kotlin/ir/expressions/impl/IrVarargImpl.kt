/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.IrVarargElement
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.utils.SmartList

class IrVarargImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val varargElementType: IrType
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrVararg {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        varargElementType: IrType,
        elements: List<IrVarargElement>
    ) : this(startOffset, endOffset, type, varargElementType) {
        this.elements.addAll(elements)
    }

    override val elements: MutableList<IrVarargElement> = SmartList()

    fun addElement(varargElement: IrVarargElement) {
        elements.add(varargElement)
    }

    override fun putElement(i: Int, element: IrVarargElement) {
        elements[i] = element
    }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitVararg(this, data)
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        elements.forEach { it.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        elements.forEachIndexed { i, irVarargElement ->
            elements[i] = irVarargElement.transform(transformer, data) as IrVarargElement
        }
    }
}