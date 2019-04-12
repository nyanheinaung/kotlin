/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.ValueArgument;
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo;

public abstract class MutableDataFlowInfoForArguments implements DataFlowInfoForArguments {

    @NotNull protected final DataFlowInfo initialDataFlowInfo;

    public MutableDataFlowInfoForArguments(@NotNull DataFlowInfo initialDataFlowInfo) {
        this.initialDataFlowInfo = initialDataFlowInfo;
    }

    public abstract void updateInfo(@NotNull ValueArgument valueArgument, @NotNull DataFlowInfo dataFlowInfo);
    public abstract void updateResultInfo(@NotNull DataFlowInfo dataFlowInfo);

    @NotNull
    @Override
    public DataFlowInfo getResultInfo() {
        return initialDataFlowInfo;
    }

    public static class WithoutArgumentsCheck extends MutableDataFlowInfoForArguments {

        public WithoutArgumentsCheck(@NotNull DataFlowInfo dataFlowInfo) {
            super(dataFlowInfo);
        }

        @Override
        public void updateInfo(@NotNull ValueArgument valueArgument, @NotNull DataFlowInfo dataFlowInfo) {
            throw new IllegalStateException();
        }

        @Override
        public void updateResultInfo(@NotNull DataFlowInfo dataFlowInfo) {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public DataFlowInfo getInfo(@NotNull ValueArgument valueArgument) {
            throw new IllegalStateException();
        }
    };
}
