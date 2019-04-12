/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.resolve.DescriptorUtils;
import org.jetbrains.kotlin.storage.LockBasedStorageManager;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.KotlinTypeKt;
import org.jetbrains.kotlin.types.Variance;

import java.util.ArrayList;
import java.util.List;

import static org.jetbrains.kotlin.resolve.descriptorUtil.DescriptorUtilsKt.getBuiltIns;

public class TypeParameterDescriptorImpl extends AbstractTypeParameterDescriptor {
    @Nullable
    private final Function1<KotlinType, Void> reportCycleError;

    @NotNull
    public static TypeParameterDescriptor createWithDefaultBound(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            boolean reified,
            @NotNull Variance variance,
            @NotNull Name name,
            int index
    ) {
        TypeParameterDescriptorImpl typeParameterDescriptor =
                createForFurtherModification(containingDeclaration, annotations, reified, variance, name, index, SourceElement.NO_SOURCE);
        typeParameterDescriptor.addUpperBound(getBuiltIns(containingDeclaration).getDefaultBound());
        typeParameterDescriptor.setInitialized();
        return typeParameterDescriptor;
    }

    public static TypeParameterDescriptorImpl createForFurtherModification(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            boolean reified,
            @NotNull Variance variance,
            @NotNull Name name,
            int index,
            @NotNull SourceElement source
    ) {
        return createForFurtherModification(containingDeclaration, annotations, reified, variance, name, index, source,
                                            /* reportSupertypeLoopError = */ null, SupertypeLoopChecker.EMPTY.INSTANCE);
    }

    public static TypeParameterDescriptorImpl createForFurtherModification(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            boolean reified,
            @NotNull Variance variance,
            @NotNull Name name,
            int index,
            @NotNull SourceElement source,
            @Nullable Function1<KotlinType, Void> reportCycleError,
            @NotNull SupertypeLoopChecker supertypeLoopsResolver
    ) {
        return new TypeParameterDescriptorImpl(containingDeclaration, annotations, reified, variance, name, index, source, reportCycleError,
                                               supertypeLoopsResolver);
    }

    private final List<KotlinType> upperBounds = new ArrayList<KotlinType>(1);
    private boolean initialized = false;

    private TypeParameterDescriptorImpl(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            boolean reified,
            @NotNull Variance variance,
            @NotNull Name name,
            int index,
            @NotNull SourceElement source,
            @Nullable Function1<KotlinType, Void> reportCycleError,
            @NotNull SupertypeLoopChecker supertypeLoopsChecker
    ) {
        super(LockBasedStorageManager.NO_LOCKS, containingDeclaration, annotations, name, variance, reified, index, source,
              supertypeLoopsChecker);
        this.reportCycleError = reportCycleError;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Type parameter descriptor is not initialized: " + nameForAssertions());
        }
    }

    private void checkUninitialized() {
        if (initialized) {
            throw new IllegalStateException("Type parameter descriptor is already initialized: " + nameForAssertions());
        }
    }

    private String nameForAssertions() {
        return getName() + " declared in " + DescriptorUtils.getFqName(getContainingDeclaration());
    }

    public void setInitialized() {
        checkUninitialized();
        initialized = true;
    }

    public void addUpperBound(@NotNull KotlinType bound) {
        checkUninitialized();
        doAddUpperBound(bound);
    }

    private void doAddUpperBound(KotlinType bound) {
        if (KotlinTypeKt.isError(bound)) return;
        upperBounds.add(bound); // TODO : Duplicates?
    }

    public void addDefaultUpperBound() {
        checkUninitialized();

        if (upperBounds.isEmpty()) {
            doAddUpperBound(getBuiltIns(getContainingDeclaration()).getDefaultBound());
        }
    }

    @Override
    protected void reportSupertypeLoopError(@NotNull KotlinType type) {
        if (reportCycleError == null) return;
        reportCycleError.invoke(type);
    }

    @NotNull
    @Override
    protected List<KotlinType> resolveUpperBounds() {
        checkInitialized();
        return upperBounds;
    }
}
