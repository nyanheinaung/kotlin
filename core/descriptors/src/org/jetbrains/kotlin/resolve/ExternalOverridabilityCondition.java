/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableDescriptor;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;

public interface ExternalOverridabilityCondition {
    enum Result {
        OVERRIDABLE, CONFLICT, INCOMPATIBLE, UNKNOWN
    }

    enum Contract {
        CONFLICTS_ONLY, SUCCESS_ONLY, BOTH
    }

    @NotNull
    Result isOverridable(
            @NotNull CallableDescriptor superDescriptor,
            @NotNull CallableDescriptor subDescriptor,
            @Nullable ClassDescriptor subClassDescriptor
    );

    @NotNull
    Contract getContract();
}
