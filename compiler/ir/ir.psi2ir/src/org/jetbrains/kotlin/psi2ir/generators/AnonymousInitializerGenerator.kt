/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffsetSkippingComments

class AnonymousInitializerGenerator(
    declarationGenerator: DeclarationGenerator
) : DeclarationGeneratorExtension(declarationGenerator) {

    fun generateAnonymousInitializerDeclaration(
        ktAnonymousInitializer: KtAnonymousInitializer,
        irClass: IrClass
    ): IrDeclaration =
        context.symbolTable.declareAnonymousInitializer(
            ktAnonymousInitializer.startOffsetSkippingComments, ktAnonymousInitializer.endOffset,
            IrDeclarationOrigin.DEFINED, irClass.descriptor
        ).buildWithScope { irAnonymousInitializer ->
            irAnonymousInitializer.parent = irClass
            val bodyGenerator = createBodyGenerator(irAnonymousInitializer.symbol)
            val statementGenerator = bodyGenerator.createStatementGenerator()
            val ktBody = ktAnonymousInitializer.body!!
            val irBlockBody = IrBlockBodyImpl(ktBody.startOffsetSkippingComments, ktBody.endOffset)
            if (ktBody is KtBlockExpression) {
                statementGenerator.generateStatements(ktBody.statements, irBlockBody)
            } else {
                irBlockBody.statements.add(statementGenerator.generateStatement(ktBody))
            }
            irAnonymousInitializer.body = irBlockBody
        }
}