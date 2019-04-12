/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.ValueArgument;

import java.util.Collections;
import java.util.List;

public class DefaultValueArgument implements ResolvedValueArgument {
    public static final DefaultValueArgument DEFAULT = new DefaultValueArgument();

    private DefaultValueArgument() {}

    @NotNull
    @Override
    public List<ValueArgument> getArguments() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "|DEFAULT|";
    }
}
