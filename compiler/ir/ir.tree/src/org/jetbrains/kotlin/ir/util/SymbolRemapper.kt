/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.ir.symbols.*

interface SymbolRemapper {
    fun getDeclaredClass(symbol: IrClassSymbol): IrClassSymbol
    fun getDeclaredFunction(symbol: IrSimpleFunctionSymbol): IrSimpleFunctionSymbol
    fun getDeclaredProperty(symbol: IrPropertySymbol): IrPropertySymbol
    fun getDeclaredField(symbol: IrFieldSymbol): IrFieldSymbol
    fun getDeclaredFile(symbol: IrFileSymbol): IrFileSymbol
    fun getDeclaredConstructor(symbol: IrConstructorSymbol): IrConstructorSymbol
    fun getDeclaredEnumEntry(symbol: IrEnumEntrySymbol): IrEnumEntrySymbol
    fun getDeclaredExternalPackageFragment(symbol: IrExternalPackageFragmentSymbol): IrExternalPackageFragmentSymbol
    fun getDeclaredVariable(symbol: IrVariableSymbol): IrVariableSymbol
    fun getDeclaredLocalDelegatedProperty(symbol: IrLocalDelegatedPropertySymbol): IrLocalDelegatedPropertySymbol
    fun getDeclaredTypeParameter(symbol: IrTypeParameterSymbol): IrTypeParameterSymbol
    fun getDeclaredValueParameter(symbol: IrValueParameterSymbol): IrValueParameterSymbol
    fun getReferencedClass(symbol: IrClassSymbol): IrClassSymbol
    fun getReferencedClassOrNull(symbol: IrClassSymbol?): IrClassSymbol?
    fun getReferencedEnumEntry(symbol: IrEnumEntrySymbol): IrEnumEntrySymbol
    fun getReferencedVariable(symbol: IrVariableSymbol): IrVariableSymbol
    fun getReferencedLocalDelegatedProperty(symbol: IrLocalDelegatedPropertySymbol): IrLocalDelegatedPropertySymbol
    fun getReferencedField(symbol: IrFieldSymbol): IrFieldSymbol
    fun getReferencedConstructor(symbol: IrConstructorSymbol): IrConstructorSymbol
    fun getReferencedValue(symbol: IrValueSymbol): IrValueSymbol
    fun getReferencedFunction(symbol: IrFunctionSymbol): IrFunctionSymbol
    fun getReferencedProperty(symbol: IrPropertySymbol): IrPropertySymbol
    fun getReferencedSimpleFunction(symbol: IrSimpleFunctionSymbol): IrSimpleFunctionSymbol
    fun getReferencedReturnableBlock(symbol: IrReturnableBlockSymbol): IrReturnableBlockSymbol
    fun getReferencedClassifier(symbol: IrClassifierSymbol): IrClassifierSymbol
}