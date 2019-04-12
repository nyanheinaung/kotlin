/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor;
import org.jetbrains.kotlin.descriptors.PropertyDescriptor;
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor;
import org.jetbrains.kotlin.load.java.structure.JavaClass;
import org.jetbrains.kotlin.load.java.structure.JavaElement;
import org.jetbrains.kotlin.load.java.structure.JavaField;
import org.jetbrains.kotlin.load.java.structure.JavaMethod;
import org.jetbrains.kotlin.name.FqName;

public interface JavaResolverCache {
    JavaResolverCache EMPTY = new JavaResolverCache() {
        @Nullable
        @Override
        public ClassDescriptor getClassResolvedFromSource(@NotNull FqName fqName) {
            return null;
        }

        @Override
        public void recordMethod(@NotNull JavaMethod method, @NotNull SimpleFunctionDescriptor descriptor) {
        }

        @Override
        public void recordConstructor(@NotNull JavaElement element, @NotNull ConstructorDescriptor descriptor) {
        }

        @Override
        public void recordField(@NotNull JavaField field, @NotNull PropertyDescriptor descriptor) {
        }

        @Override
        public void recordClass(@NotNull JavaClass javaClass, @NotNull ClassDescriptor descriptor) {
        }
    };

    @Nullable
    ClassDescriptor getClassResolvedFromSource(@NotNull FqName fqName);

    void recordMethod(@NotNull JavaMethod method, @NotNull SimpleFunctionDescriptor descriptor);

    void recordConstructor(@NotNull JavaElement element, @NotNull ConstructorDescriptor descriptor);

    void recordField(@NotNull JavaField field, @NotNull PropertyDescriptor descriptor);

    void recordClass(@NotNull JavaClass javaClass, @NotNull ClassDescriptor descriptor);
}
