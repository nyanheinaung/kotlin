/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.ModuleDescriptor;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.resolve.scopes.*;

public class MutablePackageFragmentDescriptor extends PackageFragmentDescriptorImpl {

    public MutablePackageFragmentDescriptor(@NotNull ModuleDescriptor module, @NotNull FqName fqName) {
        super(module, fqName);
    }

    @NotNull
    @Override
    public MemberScope getMemberScope() {
        return MemberScope.Empty.INSTANCE;
    }
}
