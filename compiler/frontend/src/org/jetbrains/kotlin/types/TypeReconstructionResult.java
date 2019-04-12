/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

import org.jetbrains.annotations.Nullable;

public class TypeReconstructionResult {
    private final KotlinType resultingType;
    private final boolean allArgumentsInferred;

    public TypeReconstructionResult(@Nullable KotlinType resultingType, boolean allArgumentsInferred) {
        this.resultingType = resultingType;
        this.allArgumentsInferred = allArgumentsInferred;
    }

    @Nullable
    public KotlinType getResultingType() {
        return resultingType;
    }

    public boolean isAllArgumentsInferred() {
        return allArgumentsInferred;
    }
}
