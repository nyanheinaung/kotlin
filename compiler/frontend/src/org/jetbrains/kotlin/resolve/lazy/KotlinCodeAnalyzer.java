/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.ModuleDescriptor;
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider;
import org.jetbrains.kotlin.incremental.components.LookupLocation;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.resolve.BindingContext;

public interface KotlinCodeAnalyzer extends TopLevelDescriptorProvider {

    @NotNull
    ModuleDescriptor getModuleDescriptor();

    @NotNull
    ClassDescriptor getClassDescriptor(@NotNull KtClassOrObject classOrObject, @NotNull LookupLocation location);

    @NotNull
    BindingContext getBindingContext();

    @NotNull
    DeclarationDescriptor resolveToDescriptor(@NotNull KtDeclaration declaration);

    @NotNull
    DeclarationScopeProvider getDeclarationScopeProvider();

    @NotNull
    FileScopeProvider getFileScopeProvider();

    /**
     * Forces all descriptors to be resolved.
     *
     * Use this method when laziness plays against you, e.g. when lazy descriptors may be accessed in a multi-threaded setting
     */
    void forceResolveAll();

    @NotNull
    PackageFragmentProvider getPackageFragmentProvider();
}
