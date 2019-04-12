/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.codegen.*
import org.jetbrains.kotlin.codegen.AsmUtil.boxType
import org.jetbrains.kotlin.codegen.AsmUtil.isPrimitive
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.org.objectweb.asm.Type

object JavaClassProperty : IntrinsicMethod() {
    fun invokeWith(value: PromisedValue) {
        if (value.type == Type.VOID_TYPE) {
            return invokeWith(value.coerce(AsmTypes.UNIT_TYPE))
        }
        if (isPrimitive(value.type)) {
            value.discard()
            value.mv.getstatic(boxType(value.type).internalName, "TYPE", "Ljava/lang/Class;")
        } else {
            value.materialize()
            value.mv.invokevirtual("java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        }
    }

    override fun invoke(expression: IrFunctionAccessExpression, codegen: ExpressionCodegen, data: BlockInfo): PromisedValue? {
        invokeWith(expression.extensionReceiver!!.accept(codegen, data))
        return with(codegen) { expression.onStack }
    }
}
