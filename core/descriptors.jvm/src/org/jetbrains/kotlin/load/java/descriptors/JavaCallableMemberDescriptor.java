/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.descriptors;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.List;

public interface JavaCallableMemberDescriptor extends CallableMemberDescriptor {
    @NotNull
    JavaCallableMemberDescriptor enhance(
            @Nullable KotlinType enhancedReceiverType,
            @NotNull List<ValueParameterData> enhancedValueParametersData,
            @NotNull KotlinType enhancedReturnType,
            @Nullable Pair<UserDataKey<?>, ?> additionalUserData
    );
}
