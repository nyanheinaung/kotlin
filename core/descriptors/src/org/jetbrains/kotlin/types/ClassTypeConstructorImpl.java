/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.resolve.DescriptorUtils;
import org.jetbrains.kotlin.storage.LockBasedStorageManager;
import org.jetbrains.kotlin.storage.StorageManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassTypeConstructorImpl extends AbstractClassTypeConstructor implements TypeConstructor {
    private final ClassDescriptor classDescriptor;
    private final List<TypeParameterDescriptor> parameters;
    private final Collection<KotlinType> supertypes;

    public ClassTypeConstructorImpl(
            @NotNull ClassDescriptor classDescriptor,
            @NotNull List<? extends TypeParameterDescriptor> parameters,
            @NotNull Collection<KotlinType> supertypes,
            @NotNull StorageManager storageManager
    ) {
        super(storageManager);
        this.classDescriptor = classDescriptor;
        this.parameters = Collections.unmodifiableList(new ArrayList<TypeParameterDescriptor>(parameters));
        this.supertypes = Collections.unmodifiableCollection(supertypes);
    }

    @Override
    @NotNull
    public List<TypeParameterDescriptor> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return DescriptorUtils.getFqName(classDescriptor).asString();
    }

    @Override
    public boolean isDenotable() {
        return true;
    }

    @Override
    @NotNull
    public ClassDescriptor getDeclarationDescriptor() {
        return classDescriptor;
    }

    @NotNull
    @Override
    protected Collection<KotlinType> computeSupertypes() {
        return supertypes;
    }

    @NotNull
    @Override
    protected SupertypeLoopChecker getSupertypeLoopChecker() {
        return SupertypeLoopChecker.EMPTY.INSTANCE;
    }
}
