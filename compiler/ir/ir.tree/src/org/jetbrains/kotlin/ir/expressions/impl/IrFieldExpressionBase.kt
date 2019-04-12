/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFieldAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.types.IrType

abstract class IrFieldExpressionBase(
    startOffset: Int, endOffset: Int,
    override val symbol: IrFieldSymbol,
    type: IrType,
    override val origin: IrStatementOrigin? = null,
    override val superQualifierSymbol: IrClassSymbol?
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrFieldAccessExpression {

    override val descriptor: PropertyDescriptor get() = symbol.descriptor
    override val superQualifier: ClassDescriptor? get() = superQualifierSymbol?.descriptor

    final override var receiver: IrExpression? = null
}