/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.resolve.constants.ConstantValue;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeSubstitutor;

public interface VariableDescriptor extends ValueDescriptor {
    @Override
    VariableDescriptor substitute(@NotNull TypeSubstitutor substitutor);

    boolean isVar();

    @Nullable
    ConstantValue<?> getCompileTimeInitializer();

    /**
     * @return true if iff original declaration has appropriate flags and type, e.g. `const` modifier in Kotlin.
     * It completely does not means that if isConst then `getCompileTimeInitializer` is not null
     */
    boolean isConst();

    boolean isLateInit();
}
