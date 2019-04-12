/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.Name

interface IrSymbolOwner : IrElement {
    val symbol: IrSymbol
}

interface IrMetadataSourceOwner : IrElement {
    val metadata: MetadataSource?
}

interface IrDeclaration : IrStatement, IrAnnotationContainer, IrMetadataSourceOwner {
    val descriptor: DeclarationDescriptor
    var origin: IrDeclarationOrigin

    var parent: IrDeclarationParent

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrStatement =
        accept(transformer, data) as IrStatement
}

interface IrSymbolDeclaration<out S : IrSymbol> : IrDeclaration, IrSymbolOwner {
    override val symbol: S
}

interface IrOverridableDeclaration<S : IrSymbol> : IrDeclaration {
    val overriddenSymbols: MutableList<S>
}

interface IrDeclarationWithVisibility : IrDeclaration {
    val visibility: Visibility
}

interface IrDeclarationWithName : IrDeclaration {
    val name: Name
}

