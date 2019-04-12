/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType

interface IrClass :
    IrSymbolDeclaration<IrClassSymbol>, IrDeclarationWithName, IrDeclarationWithVisibility,
    IrDeclarationContainer, IrTypeParametersContainer {

    override val descriptor: ClassDescriptor

    val kind: ClassKind
    val modality: Modality
    val isCompanion: Boolean
    val isInner: Boolean
    val isData: Boolean
    val isExternal: Boolean
    val isInline: Boolean

    val superTypes: MutableList<IrType>

    var thisReceiver: IrValueParameter?
}

fun IrClass.addMember(member: IrDeclaration) {
    declarations.add(member)
}

fun IrClass.addAll(members: List<IrDeclaration>) {
    declarations.addAll(members)
}

fun IrClass.getInstanceInitializerMembers() =
    declarations.filter {
        when (it) {
            is IrAnonymousInitializer ->
                true
            is IrProperty ->
                it.backingField?.initializer != null
            is IrField ->
                it.initializer != null
            else -> false
        }
    }

