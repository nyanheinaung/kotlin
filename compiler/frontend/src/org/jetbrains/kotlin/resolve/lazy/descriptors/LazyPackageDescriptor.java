/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.ModuleDescriptor;
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.resolve.lazy.ForceResolveUtil;
import org.jetbrains.kotlin.resolve.lazy.LazyEntity;
import org.jetbrains.kotlin.resolve.lazy.ResolveSession;
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider;
import org.jetbrains.kotlin.resolve.scopes.MemberScope;

public class LazyPackageDescriptor extends PackageFragmentDescriptorImpl implements LazyEntity {
    private final MemberScope memberScope;
    private final PackageMemberDeclarationProvider declarationProvider;

    public LazyPackageDescriptor(
            @NotNull ModuleDescriptor module,
            @NotNull FqName fqName,
            @NotNull ResolveSession resolveSession,
            @NotNull PackageMemberDeclarationProvider declarationProvider
    ) {
        super(module, fqName);
        this.declarationProvider = declarationProvider;

        this.memberScope = new LazyPackageMemberScope(resolveSession, declarationProvider, this);
    }

    @NotNull
    @Override
    public MemberScope getMemberScope() {
        return memberScope;
    }

    @Override
    public void forceResolveAllContents() {
        ForceResolveUtil.forceResolveAllContents(memberScope);
    }

    @NotNull
    public PackageMemberDeclarationProvider getDeclarationProvider() {
        return declarationProvider;
    }
}
