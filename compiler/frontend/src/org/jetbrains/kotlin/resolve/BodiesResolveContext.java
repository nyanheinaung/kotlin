/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import kotlin.annotations.jvm.Mutable;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo;
import org.jetbrains.kotlin.resolve.scopes.LexicalScope;

import java.util.Collection;
import java.util.Map;

public interface BodiesResolveContext {
    @ReadOnly
    Collection<KtFile> getFiles();

    @Mutable
    Map<KtClassOrObject, ClassDescriptorWithResolutionScopes> getDeclaredClasses();
    @Mutable
    Map<KtAnonymousInitializer, ClassDescriptorWithResolutionScopes> getAnonymousInitializers();
    @Mutable
    Map<KtSecondaryConstructor, ClassConstructorDescriptor> getSecondaryConstructors();
    @Mutable
    Map<KtScript, ClassDescriptorWithResolutionScopes> getScripts();

    @Mutable
    Map<KtProperty, PropertyDescriptor> getProperties();
    @Mutable
    Map<KtNamedFunction, SimpleFunctionDescriptor> getFunctions();
    @Mutable
    Map<KtTypeAlias, TypeAliasDescriptor> getTypeAliases();
    @Mutable
    Map<KtDestructuringDeclarationEntry, PropertyDescriptor> getDestructuringDeclarationEntries();

    @Nullable
    LexicalScope getDeclaringScope(@NotNull KtDeclaration declaration);

    @NotNull
    DataFlowInfo getOuterDataFlowInfo();

    @NotNull
    TopDownAnalysisMode getTopDownAnalysisMode();
}
