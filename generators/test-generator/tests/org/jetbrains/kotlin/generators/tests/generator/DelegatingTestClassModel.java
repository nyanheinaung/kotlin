/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tests.generator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DelegatingTestClassModel implements TestClassModel {
    private final TestClassModel delegate;

    public DelegatingTestClassModel(TestClassModel delegate) {
        this.delegate = delegate;
    }

    @NotNull
    @Override
    public String getName() {
        return delegate.getName();
    }

    @NotNull
    @Override
    public Collection<TestClassModel> getInnerTestClasses() {
        return delegate.getInnerTestClasses();
    }

    @NotNull
    @Override
    public Collection<MethodModel> getMethods() {
        return delegate.getMethods();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Nullable
    @Override
    public String getDataPathRoot() {
        return delegate.getDataPathRoot();
    }

    @Override
    public String getDataString() {
        return delegate.getDataString();
    }
}
