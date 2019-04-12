/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol

interface IrCall : IrFunctionAccessExpression {
    val superQualifier: ClassDescriptor?
    val superQualifierSymbol: IrClassSymbol?
}

interface IrCallWithShallowCopy : IrCall {
    fun shallowCopy(newOrigin: IrStatementOrigin?, newCallee: IrFunctionSymbol, newSuperQualifier: IrClassSymbol?): IrCall
}
