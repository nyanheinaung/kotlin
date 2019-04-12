/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.backend.common.atMostOne
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.Name

fun IrClassSymbol.getPropertyDeclaration(name: String) =
    this.owner.declarations.filterIsInstance<IrProperty>()
        .atMostOne { it.descriptor.name == Name.identifier(name) }

fun IrClassSymbol.getSimpleFunction(name: String): IrSimpleFunctionSymbol? =
        owner.findDeclaration<IrSimpleFunction> { it.name.asString() == name }?.symbol

fun IrClassSymbol.getPropertyGetter(name: String): IrSimpleFunctionSymbol? =
    this.getPropertyDeclaration(name)?.getter?.symbol ?: this.getSimpleFunction("<get-$name>")

fun IrClassSymbol.getPropertySetter(name: String): IrSimpleFunctionSymbol? =
    this.getPropertyDeclaration(name)?.setter?.symbol ?: this.getSimpleFunction("<set-$name>")

fun IrClassSymbol.getPropertyField(name: String): IrFieldSymbol? =
    this.getPropertyDeclaration(name)?.backingField?.symbol
