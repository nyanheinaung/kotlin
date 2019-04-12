/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.PropertyAccessorDescriptor
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.org.objectweb.asm.Type

class IrIntrinsicMethods(irBuiltIns: IrBuiltIns) {

    val intrinsics = IntrinsicMethods()

    private val irMapping = hashMapOf<CallableMemberDescriptor, IntrinsicMethod>()

    private fun createPrimitiveComparisonIntrinsics(typeToIrFun: Map<SimpleType, IrSimpleFunctionSymbol>, operator: KtSingleValueToken) {
        for ((type, irFunSymbol) in typeToIrFun) {
            irMapping[irFunSymbol.descriptor] = PrimitiveComparison(type, operator)
        }
    }

    init {
        irMapping[irBuiltIns.eqeq] = Equals(KtTokens.EQEQ)
        irMapping[irBuiltIns.eqeqeq] = Equals(KtTokens.EQEQEQ)
        irMapping[irBuiltIns.ieee754equalsFunByOperandType[irBuiltIns.float]!!.descriptor] = Ieee754Equals(Type.FLOAT_TYPE)
        irMapping[irBuiltIns.ieee754equalsFunByOperandType[irBuiltIns.double]!!.descriptor] = Ieee754Equals(Type.DOUBLE_TYPE)
        irMapping[irBuiltIns.booleanNot] = Not()

        createPrimitiveComparisonIntrinsics(irBuiltIns.lessFunByOperandType, KtTokens.LT)
        createPrimitiveComparisonIntrinsics(irBuiltIns.lessOrEqualFunByOperandType, KtTokens.LTEQ)
        createPrimitiveComparisonIntrinsics(irBuiltIns.greaterFunByOperandType, KtTokens.GT)
        createPrimitiveComparisonIntrinsics(irBuiltIns.greaterOrEqualFunByOperandType, KtTokens.GTEQ)

        irMapping[irBuiltIns.enumValueOf] = IrEnumValueOf()
        irMapping[irBuiltIns.noWhenBranchMatchedException] = IrNoWhenBranchMatchedException()
        irMapping[irBuiltIns.illegalArgumentException] = IrIllegalArgumentException()
        irMapping[irBuiltIns.throwNpe] = ThrowNPE()
    }

    fun getIntrinsic(descriptor: CallableMemberDescriptor): IntrinsicMethod? {
        intrinsics.getIntrinsic(descriptor)?.let { return it }
        if (descriptor is PropertyAccessorDescriptor) {
            return intrinsics.getIntrinsic(DescriptorUtils.unwrapFakeOverride(descriptor.correspondingProperty))
        }
        return irMapping[descriptor.original]
    }
}
