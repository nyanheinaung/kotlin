/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.resolve.scopes.LexicalScope;

public class TypeResolutionContext {
    public final LexicalScope scope;
    public final BindingTrace trace;
    public final boolean checkBounds;
    public final boolean allowBareTypes;
    public final boolean isDebuggerContext;
    public final boolean abbreviated;

    public TypeResolutionContext(@NotNull LexicalScope scope, @NotNull BindingTrace trace, boolean checkBounds, boolean allowBareTypes, boolean isDebuggerContext) {
        this(scope, trace, checkBounds, allowBareTypes, isDebuggerContext, false);
    }

    public TypeResolutionContext(
            @NotNull LexicalScope scope,
            @NotNull BindingTrace trace,
            boolean checkBounds,
            boolean allowBareTypes,
            boolean isDebuggerContext,
            boolean abbreviated
    ) {
        this.scope = scope;
        this.trace = trace;
        this.checkBounds = checkBounds;
        this.allowBareTypes = allowBareTypes;
        this.isDebuggerContext = isDebuggerContext;
        this.abbreviated = abbreviated;
    }

    @NotNull
    public TypeResolutionContext noBareTypes() {
        return new TypeResolutionContext(scope, trace, checkBounds, false, isDebuggerContext, abbreviated);
    }
}
