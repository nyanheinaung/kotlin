/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline;

import org.jetbrains.annotations.Nullable;

class InvokeCall {
    public final FunctionalArgument functionalArgument;
    public final int finallyDepthShift;

    InvokeCall(@Nullable FunctionalArgument functionalArgument, int finallyDepthShift) {
        this.functionalArgument = functionalArgument;
        this.finallyDepthShift = finallyDepthShift;
    }
}
