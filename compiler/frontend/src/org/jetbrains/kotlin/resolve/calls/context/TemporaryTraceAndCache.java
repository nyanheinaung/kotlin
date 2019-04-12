/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.context;

import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.resolve.TemporaryBindingTrace;

public class TemporaryTraceAndCache {
    public final TemporaryBindingTrace trace;
    public final TemporaryResolutionResultsCache cache;

    public TemporaryTraceAndCache(ResolutionContext context, String debugName, KtExpression expression) {
        trace = TemporaryBindingTrace.create(context.trace, debugName, expression);
        cache = new TemporaryResolutionResultsCache(context.resolutionResultsCache);
    }

    public static TemporaryTraceAndCache create(ResolutionContext context, String debugName, KtExpression expression) {
        return new TemporaryTraceAndCache(context, debugName, expression);
    }

    public void commit() {
        trace.commit();
        cache.commit();
    }
}
