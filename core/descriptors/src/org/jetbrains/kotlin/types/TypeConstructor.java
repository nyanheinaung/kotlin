/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.types.model.TypeConstructorMarker;

import java.util.Collection;
import java.util.List;

public interface TypeConstructor extends TypeConstructorMarker {
    /**
     * It may differ from ClassDescriptor.declaredParameters if the class is inner, in such case
     * it also contains additional parameters from outer declarations.
     *
     * @return list of parameters for type constructor, both from current declaration and the outer one
     */
    @NotNull
    @ReadOnly
    List<TypeParameterDescriptor> getParameters();

    @NotNull
    @ReadOnly
    Collection<KotlinType> getSupertypes();

    /**
     * Cannot have subtypes.
     */
    boolean isFinal();

    /**
     * If the type is non-denotable, it can't be written in code directly, it only can appear internally inside a type checker.
     * Examples: intersection type or number value type.
     */
    boolean isDenotable();

    @Nullable
    ClassifierDescriptor getDeclarationDescriptor();

    @NotNull
    KotlinBuiltIns getBuiltIns();
}
