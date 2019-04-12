/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.lower

import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.phaser.makeIrFilePhase
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrCallableReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCompositeImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

val kCallableNamePropertyPhase = makeIrFilePhase(
    ::KCallableNamePropertyLowering,
    name = "KCallableNameProperty",
    description = "Replace name references for callables with constants"
)

private class KCallableNamePropertyLowering(val context: BackendContext) : FileLoweringPass {
    override fun lower(irFile: IrFile) {
        irFile.transformChildrenVoid(KCallableNamePropertyTransformer(this))
    }
}

private class KCallableNamePropertyTransformer(val lower: KCallableNamePropertyLowering) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {

        val callableReference = expression.dispatchReceiver as? IrCallableReference ?: return expression

        //TODO rewrite checking
        val directMember = expression.symbol.owner.let {
            (it as? IrSimpleFunction)?.correspondingProperty ?: it
        }
        val irClass = directMember.parent as? IrClass ?: return expression
        if (!irClass.isSubclassOf(lower.context.irBuiltIns.kCallableClass.owner)) return expression
        val name = when (directMember) {
            is IrSimpleFunction -> directMember.name
            is IrProperty -> directMember.name
            else -> throw AssertionError("Should be IrSimpleFunction or IrProperty, got $directMember")
        }
        if (name.asString() != "name") return expression

        val receiver = callableReference.dispatchReceiver ?: callableReference.extensionReceiver

        return IrCompositeImpl(expression.startOffset, expression.endOffset, lower.context.irBuiltIns.stringType).apply {
            receiver?.let {
                //put receiver for bound callable reference
                statements.add(it)
            }

            statements.add(
                IrConstImpl.string(
                    expression.startOffset,
                    expression.endOffset,
                    lower.context.irBuiltIns.stringType,
                    callableReference.descriptor.name.asString()
                )
            )
        }
    }
}
