/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

public interface DeclarationDescriptorVisitor<R, D> {
    R visitPackageFragmentDescriptor(PackageFragmentDescriptor descriptor, D data);

    R visitPackageViewDescriptor(PackageViewDescriptor descriptor, D data);

    R visitVariableDescriptor(VariableDescriptor descriptor, D data);

    R visitFunctionDescriptor(FunctionDescriptor descriptor, D data);

    R visitTypeParameterDescriptor(TypeParameterDescriptor descriptor, D data);

    R visitClassDescriptor(ClassDescriptor descriptor, D data);

    R visitTypeAliasDescriptor(TypeAliasDescriptor descriptor, D data);

    R visitModuleDeclaration(ModuleDescriptor descriptor, D data);

    R visitConstructorDescriptor(ConstructorDescriptor constructorDescriptor, D data);

    R visitScriptDescriptor(ScriptDescriptor scriptDescriptor, D data);

    R visitPropertyDescriptor(PropertyDescriptor descriptor, D data);

    R visitValueParameterDescriptor(ValueParameterDescriptor descriptor, D data);

    R visitPropertyGetterDescriptor(PropertyGetterDescriptor descriptor, D data);

    R visitPropertySetterDescriptor(PropertySetterDescriptor descriptor, D data);

    R visitReceiverParameterDescriptor(ReceiverParameterDescriptor descriptor, D data);
}
