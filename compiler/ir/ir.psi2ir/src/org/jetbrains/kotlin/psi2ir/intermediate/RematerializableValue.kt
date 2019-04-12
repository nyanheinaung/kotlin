/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionWithCopy
import org.jetbrains.kotlin.ir.expressions.impl.IrContainerExpressionBase
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.types.KotlinType

class RematerializableValue(val irExpression: IrExpressionWithCopy) : IntermediateValue {

    override val type: IrType get() = irExpression.type

    override fun load(): IrExpression = irExpression.copy()
}

fun Scope.createTemporaryVariableInBlock(
    context: IrGeneratorContext,
    irExpression: IrExpression,
    block: IrContainerExpressionBase,
    nameHint: String? = null
): IntermediateValue {
    val temporaryVariable = createTemporaryVariable(irExpression, nameHint)
    block.statements.add(temporaryVariable)
    return VariableLValue(context, temporaryVariable)
}