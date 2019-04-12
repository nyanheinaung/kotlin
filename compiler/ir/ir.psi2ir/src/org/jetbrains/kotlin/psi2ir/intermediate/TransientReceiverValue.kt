/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.types.KotlinType

class TransientReceiverValue(override val type: IrType) : IntermediateValue {

    override fun load(): IrExpression {
        throw AssertionError("Transient receiver should not be instantiated")
    }

    override fun loadIfExists(): IrExpression? = null
}
