/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeConstructor;
import org.jetbrains.kotlin.types.Variance;
import org.jetbrains.kotlin.types.model.TypeParameterMarker;

import java.util.List;

public interface TypeParameterDescriptor extends ClassifierDescriptor, TypeParameterMarker {
    boolean isReified();

    @NotNull
    Variance getVariance();

    @NotNull
    List<KotlinType> getUpperBounds();

    @NotNull
    @Override
    TypeConstructor getTypeConstructor();

    @NotNull
    @Override
    TypeParameterDescriptor getOriginal();

    int getIndex();

    /**
     * Is current parameter just a copy of another type parameter (getOriginal) from outer declaration
     * to be used for type constructor of inner declaration (i.e. inner class).
     *
     * If this method returns true:
     * 1. Containing declaration for current parameter is the inner one
     * 2. 'getOriginal' returns original type parameter from outer declaration
     * 3. 'getTypeConstructor' is the same as for original declaration (at least in means of 'equals')
     */
    boolean isCapturedFromOuterDeclaration();
}
