/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType

abstract class PropertyLValueBase(
    protected val context: IrGeneratorContext,
    val scope: Scope,
    val startOffset: Int,
    val endOffset: Int,
    val origin: IrStatementOrigin?,
    override val type: IrType,
    val callReceiver: CallReceiver,
    val superQualifier: IrClassSymbol?
) : LValue, AssignmentReceiver {
    override fun assign(withLValue: (LValue) -> IrExpression) =
        callReceiver.call { dispatchReceiverValue, extensionReceiverValue ->
            val dispatchReceiverVariable2 = dispatchReceiverValue?.let {
                scope.createTemporaryVariable(dispatchReceiverValue.load(), "this")
            }
            val dispatchReceiverValue2 = dispatchReceiverVariable2?.let { VariableLValue(context, it) }

            val extensionReceiverVariable2 = extensionReceiverValue?.let {
                scope.createTemporaryVariable(extensionReceiverValue.load(), "receiver")
            }
            val extensionReceiverValue2 = extensionReceiverVariable2?.let { VariableLValue(context, it) }

            val irResultExpression = withLValue(withReceiver(dispatchReceiverValue2, extensionReceiverValue2))

            val irBlock = IrBlockImpl(startOffset, endOffset, irResultExpression.type, origin)
            irBlock.addIfNotNull(dispatchReceiverVariable2)
            irBlock.addIfNotNull(extensionReceiverVariable2)
            irBlock.statements.add(irResultExpression)
            irBlock
        }

    override fun assign(value: IrExpression): IrExpression =
        store(value)

    protected abstract fun withReceiver(dispatchReceiver: VariableLValue?, extensionReceiver: VariableLValue?): PropertyLValueBase
}

class FieldPropertyLValue(
    context: IrGeneratorContext,
    scope: Scope,
    startOffset: Int,
    endOffset: Int,
    origin: IrStatementOrigin?,
    val field: IrFieldSymbol,
    type: IrType,
    callReceiver: CallReceiver,
    superQualifier: IrClassSymbol?
) :
    PropertyLValueBase(context, scope, startOffset, endOffset, origin, type, callReceiver, superQualifier) {

    override fun load(): IrExpression =
        callReceiver.call { dispatchReceiverValue, extensionReceiverValue ->
            assert(extensionReceiverValue == null) { "Field can't have an extension receiver: ${field.descriptor}" }
            IrGetFieldImpl(
                startOffset, endOffset,
                field,
                type,
                dispatchReceiverValue?.load(),
                origin,
                superQualifier
            )
        }

    override fun store(irExpression: IrExpression) =
        callReceiver.call { dispatchReceiverValue, extensionReceiverValue ->
            assert(extensionReceiverValue == null) { "Field can't have an extension receiver: ${field.descriptor}" }
            IrSetFieldImpl(
                startOffset, endOffset,
                field,
                dispatchReceiverValue?.load(),
                irExpression,
                context.irBuiltIns.unitType,
                origin,
                superQualifier
            )
        }

    override fun withReceiver(dispatchReceiver: VariableLValue?, extensionReceiver: VariableLValue?): PropertyLValueBase =
        FieldPropertyLValue(
            context,
            scope, startOffset, endOffset, origin,
            field,
            type,
            SimpleCallReceiver(dispatchReceiver, extensionReceiver),
            superQualifier
        )
}

class AccessorPropertyLValue(
    context: IrGeneratorContext,
    scope: Scope,
    startOffset: Int,
    endOffset: Int,
    origin: IrStatementOrigin?,
    type: IrType,
    val getter: IrFunctionSymbol?,
    val getterDescriptor: FunctionDescriptor?,
    val setter: IrFunctionSymbol?,
    val setterDescriptor: FunctionDescriptor?,
    val typeArguments: List<IrType>?,
    callReceiver: CallReceiver,
    superQualifier: IrClassSymbol?
) : PropertyLValueBase(context, scope, startOffset, endOffset, origin, type, callReceiver, superQualifier) {

    private val typeArgumentsCount = typeArguments?.size ?: 0

    private fun IrMemberAccessExpression.putTypeArguments() {
        typeArguments?.forEachIndexed { index, irType ->
            putTypeArgument(index, irType)
        }
    }

    override fun load(): IrExpression =
        callReceiver.adjustForCallee(getterDescriptor!!).call { dispatchReceiverValue, extensionReceiverValue ->
            IrGetterCallImpl(
                startOffset, endOffset,
                type,
                getter!!, getterDescriptor,
                typeArgumentsCount,
                dispatchReceiverValue?.load(),
                extensionReceiverValue?.load(),
                origin,
                superQualifier
            ).apply {
                putTypeArguments()
            }
        }

    override fun store(irExpression: IrExpression) =
        callReceiver.adjustForCallee(setterDescriptor!!).call { dispatchReceiverValue, extensionReceiverValue ->
            IrSetterCallImpl(
                startOffset, endOffset,
                context.irBuiltIns.unitType,
                setter!!, setterDescriptor,
                typeArgumentsCount,
                dispatchReceiverValue?.load(),
                extensionReceiverValue?.load(),
                irExpression,
                origin,
                superQualifier
            ).apply {
                putTypeArguments()
            }
        }

    override fun withReceiver(dispatchReceiver: VariableLValue?, extensionReceiver: VariableLValue?): PropertyLValueBase =
        AccessorPropertyLValue(
            context, scope,
            startOffset, endOffset, origin,
            type, getter, getterDescriptor, setter, setterDescriptor,
            typeArguments,
            SimpleCallReceiver(dispatchReceiver, extensionReceiver),
            superQualifier
        )
}