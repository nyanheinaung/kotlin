/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;

import java.util.List;

public interface ErrorReporter {
    void reportIncompleteHierarchy(@NotNull ClassDescriptor descriptor, @NotNull List<String> unresolvedSuperClasses);

    void reportCannotInferVisibility(@NotNull CallableMemberDescriptor descriptor);

    ErrorReporter DO_NOTHING = new ErrorReporter() {
        @Override
        public void reportIncompleteHierarchy(@NotNull ClassDescriptor descriptor, @NotNull List<String> unresolvedSuperClasses) {
        }

        @Override
        public void reportCannotInferVisibility(@NotNull CallableMemberDescriptor descriptor) {
        }
    };
}
