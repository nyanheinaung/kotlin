/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline;

import org.jetbrains.annotations.NotNull;

public interface ReturnLabelOwner {
    boolean isReturnFromMe(@NotNull String labelName);

    ReturnLabelOwner SKIP_ALL = name -> false;

    ReturnLabelOwner NOT_APPLICABLE = name -> {
        throw new RuntimeException("This operation not applicable for current context");
    };
}
