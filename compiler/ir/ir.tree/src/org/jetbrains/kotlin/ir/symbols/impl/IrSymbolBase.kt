/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.symbols.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.descriptors.IrBasedDeclarationDescriptor
import org.jetbrains.kotlin.ir.expressions.IrReturnableBlock
import org.jetbrains.kotlin.ir.symbols.*

abstract class IrSymbolBase<out D : DeclarationDescriptor>(override val descriptor: D) : IrSymbol

abstract class IrBindableSymbolBase<out D : DeclarationDescriptor, B : IrSymbolOwner>(descriptor: D) :
    IrBindableSymbol<D, B>, IrSymbolBase<D>(descriptor) {

    init {
        assert(isOriginalDescriptor(descriptor)) {
            "Substituted descriptor $descriptor for ${descriptor.original}"
        }
    }

    private fun isOriginalDescriptor(descriptor: DeclarationDescriptor): Boolean =
        descriptor is IrBasedDeclarationDescriptor ||
                descriptor is ValueParameterDescriptor && isOriginalDescriptor(descriptor.containingDeclaration) ||
                descriptor == descriptor.original

    private var _owner: B? = null
    override val owner: B
        get() = _owner ?: throw IllegalStateException("Symbol for $descriptor is unbound")

    override fun bind(owner: B) {
        if (_owner == null) {
            _owner = owner
        } else {
            throw IllegalStateException("${javaClass.simpleName} for $descriptor is already bound")
        }
    }

    override val isBound: Boolean
        get() = _owner != null
}

class IrFileSymbolImpl(descriptor: PackageFragmentDescriptor) :
    IrBindableSymbolBase<PackageFragmentDescriptor, IrFile>(descriptor),
    IrFileSymbol

class IrExternalPackageFragmentSymbolImpl(descriptor: PackageFragmentDescriptor) :
    IrBindableSymbolBase<PackageFragmentDescriptor, IrExternalPackageFragment>(descriptor),
    IrExternalPackageFragmentSymbol

class IrAnonymousInitializerSymbolImpl(descriptor: ClassDescriptor) :
    IrBindableSymbolBase<ClassDescriptor, IrAnonymousInitializer>(descriptor),
    IrAnonymousInitializerSymbol {
    constructor(irClassSymbol: IrClassSymbol) : this(irClassSymbol.descriptor) {}
}

class IrClassSymbolImpl(descriptor: ClassDescriptor) :
    IrBindableSymbolBase<ClassDescriptor, IrClass>(descriptor),
    IrClassSymbol

class IrEnumEntrySymbolImpl(descriptor: ClassDescriptor) :
    IrBindableSymbolBase<ClassDescriptor, IrEnumEntry>(descriptor),
    IrEnumEntrySymbol

class IrFieldSymbolImpl(descriptor: PropertyDescriptor) :
    IrBindableSymbolBase<PropertyDescriptor, IrField>(descriptor),
    IrFieldSymbol

class IrTypeParameterSymbolImpl(descriptor: TypeParameterDescriptor) :
    IrBindableSymbolBase<TypeParameterDescriptor, IrTypeParameter>(descriptor),
    IrTypeParameterSymbol

class IrValueParameterSymbolImpl(descriptor: ParameterDescriptor) :
    IrBindableSymbolBase<ParameterDescriptor, IrValueParameter>(descriptor),
    IrValueParameterSymbol

class IrVariableSymbolImpl(descriptor: VariableDescriptor) :
    IrBindableSymbolBase<VariableDescriptor, IrVariable>(descriptor),
    IrVariableSymbol

class IrSimpleFunctionSymbolImpl(descriptor: FunctionDescriptor) :
    IrBindableSymbolBase<FunctionDescriptor, IrSimpleFunction>(descriptor),
    IrSimpleFunctionSymbol

class IrConstructorSymbolImpl(descriptor: ClassConstructorDescriptor) :
    IrBindableSymbolBase<ClassConstructorDescriptor, IrConstructor>(descriptor),
    IrConstructorSymbol

class IrReturnableBlockSymbolImpl(descriptor: FunctionDescriptor) :
    IrBindableSymbolBase<FunctionDescriptor, IrReturnableBlock>(descriptor),
    IrReturnableBlockSymbol

class IrPropertySymbolImpl(descriptor: PropertyDescriptor) :
    IrBindableSymbolBase<PropertyDescriptor, IrProperty>(descriptor),
    IrPropertySymbol

class IrLocalDelegatedPropertySymbolImpl(descriptor: VariableDescriptorWithAccessors) :
    IrBindableSymbolBase<VariableDescriptorWithAccessors, IrLocalDelegatedProperty>(descriptor),
    IrLocalDelegatedPropertySymbol