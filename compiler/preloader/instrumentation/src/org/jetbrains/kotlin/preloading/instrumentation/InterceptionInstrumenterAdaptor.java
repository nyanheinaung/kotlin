/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading.instrumentation;

import java.io.PrintStream;
import java.util.Collections;

public abstract class InterceptionInstrumenterAdaptor implements Instrumenter {
    private final InterceptionInstrumenter instrumenter;

    public InterceptionInstrumenterAdaptor() {
        this.instrumenter = new InterceptionInstrumenter(Collections.singletonList(getClass()));
    }

    @Override
    public byte[] instrument(String resourceName, byte[] data) {
        return instrumenter.instrument(resourceName, data);
    }

    @Override
    public void dump(PrintStream out) {
        instrumenter.dump(out);
    }
}
