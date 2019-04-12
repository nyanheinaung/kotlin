/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffsetSkippingComments

class LocalClassGenerator(statementGenerator: StatementGenerator) : StatementGeneratorExtension(statementGenerator) {
    fun generateObjectLiteral(ktObjectLiteral: KtObjectLiteralExpression): IrStatement {
        val startOffset = ktObjectLiteral.startOffsetSkippingComments
        val endOffset = ktObjectLiteral.endOffset
        val objectLiteralType = getInferredTypeWithImplicitCastsOrFail(ktObjectLiteral).toIrType()
        val irBlock = IrBlockImpl(startOffset, endOffset, objectLiteralType, IrStatementOrigin.OBJECT_LITERAL)

        val irClass = DeclarationGenerator(statementGenerator.context).generateClassOrObjectDeclaration(ktObjectLiteral.objectDeclaration)
        irBlock.statements.add(irClass)

        val objectConstructor = irClass.descriptor.unsubstitutedPrimaryConstructor
                ?: throw AssertionError("Object literal should have a primary constructor: ${irClass.descriptor}")
        assert(objectConstructor.dispatchReceiverParameter == null) {
            "Object literal constructor should have no dispatch receiver parameter: $objectConstructor"
        }
        assert(objectConstructor.extensionReceiverParameter == null) {
            "Object literal constructor should have no extension receiver parameter: $objectConstructor"
        }
        assert(objectConstructor.valueParameters.size == 0) {
            "Object literal constructor should have no value parameters: $objectConstructor"
        }

        irBlock.statements.add(
            IrCallImpl(
                startOffset, endOffset, objectLiteralType,
                context.symbolTable.referenceConstructor(objectConstructor),
                objectConstructor,
                IrStatementOrigin.OBJECT_LITERAL
            )
        )

        return irBlock
    }

    fun generateLocalClass(ktClassOrObject: KtClassOrObject): IrStatement =
        DeclarationGenerator(statementGenerator.context).generateClassOrObjectDeclaration(ktClassOrObject)

}
