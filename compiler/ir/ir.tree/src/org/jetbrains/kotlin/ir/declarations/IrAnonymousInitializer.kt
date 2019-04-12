/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.symbols.IrAnonymousInitializerSymbol

interface IrAnonymousInitializer : IrSymbolDeclaration<IrAnonymousInitializerSymbol> {
    override val descriptor: ClassDescriptor // TODO special descriptor for anonymous initializer blocks
    val isStatic: Boolean

    var body: IrBlockBody
}

