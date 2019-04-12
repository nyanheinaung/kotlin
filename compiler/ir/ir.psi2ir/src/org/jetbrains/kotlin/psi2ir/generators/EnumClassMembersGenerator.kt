/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.addMember
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBodyKind
import org.jetbrains.kotlin.ir.expressions.impl.IrSyntheticBodyImpl
import org.jetbrains.kotlin.ir.util.declareSimpleFunctionWithOverrides
import org.jetbrains.kotlin.psi2ir.findFirstFunction

class EnumClassMembersGenerator(declarationGenerator: DeclarationGenerator) : DeclarationGeneratorExtension(declarationGenerator) {
    fun generateSpecialMembers(irClass: IrClass) {
        generateValues(irClass)
        generateValueOf(irClass)
    }

    private fun generateValues(irClass: IrClass) {
        val valuesFunction = irClass.descriptor.staticScope.findFirstFunction("values") {
            it.dispatchReceiverParameter == null &&
                    it.extensionReceiverParameter == null &&
                    it.valueParameters.size == 0
        }

        irClass.addMember(
            context.symbolTable.declareSimpleFunctionWithOverrides(
                irClass.startOffset, irClass.endOffset,
                IrDeclarationOrigin.ENUM_CLASS_SPECIAL_MEMBER,
                valuesFunction
            ).also { irFunction ->
                FunctionGenerator(declarationGenerator).generateFunctionParameterDeclarationsAndReturnType(irFunction, null, null)
                irFunction.body = IrSyntheticBodyImpl(irClass.startOffset, irClass.endOffset, IrSyntheticBodyKind.ENUM_VALUES)
            }
        )
    }

    private fun generateValueOf(irClass: IrClass) {
        val valueOfFunction = irClass.descriptor.staticScope.findFirstFunction("valueOf") {
            it.dispatchReceiverParameter == null &&
                    it.extensionReceiverParameter == null &&
                    it.valueParameters.size == 1
        }

        irClass.addMember(
            context.symbolTable.declareSimpleFunctionWithOverrides(
                irClass.startOffset, irClass.endOffset,
                IrDeclarationOrigin.ENUM_CLASS_SPECIAL_MEMBER,
                valueOfFunction
            ).also { irFunction ->
                FunctionGenerator(declarationGenerator).generateFunctionParameterDeclarationsAndReturnType(irFunction, null, null)
                irFunction.body = IrSyntheticBodyImpl(irClass.startOffset, irClass.endOffset, IrSyntheticBodyKind.ENUM_VALUEOF)
            }
        )
    }
}
