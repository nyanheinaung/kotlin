/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.kotlin.descriptors.*;

public class DeclarationDescriptorVisitorEmptyBodies<R, D> implements DeclarationDescriptorVisitor<R, D> {
    public R visitDeclarationDescriptor(DeclarationDescriptor descriptor, D data) {
        return null;
    }

    @Override
    public R visitVariableDescriptor(VariableDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitFunctionDescriptor(FunctionDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitTypeParameterDescriptor(TypeParameterDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitPackageFragmentDescriptor(PackageFragmentDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitPackageViewDescriptor(PackageViewDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitClassDescriptor(ClassDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitTypeAliasDescriptor(TypeAliasDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitModuleDeclaration(ModuleDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }

    @Override
    public R visitConstructorDescriptor(ConstructorDescriptor constructorDescriptor, D data) {
        return visitFunctionDescriptor(constructorDescriptor, data);
    }

    @Override
    public R visitScriptDescriptor(ScriptDescriptor scriptDescriptor, D data) {
        return visitClassDescriptor(scriptDescriptor, data);
    }

    @Override
    public R visitPropertyDescriptor(PropertyDescriptor descriptor, D data) {
        return visitVariableDescriptor(descriptor, data);
    }

    @Override
    public R visitValueParameterDescriptor(ValueParameterDescriptor descriptor, D data) {
        return visitVariableDescriptor(descriptor, data);
    }

    @Override
    public R visitPropertyGetterDescriptor(PropertyGetterDescriptor descriptor, D data) {
        return visitFunctionDescriptor(descriptor, data);
    }

    @Override
    public R visitPropertySetterDescriptor(PropertySetterDescriptor descriptor, D data) {
        return visitFunctionDescriptor(descriptor, data);
    }

    @Override
    public R visitReceiverParameterDescriptor(ReceiverParameterDescriptor descriptor, D data) {
        return visitDeclarationDescriptor(descriptor, data);
    }
}
