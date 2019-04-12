/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading.instrumentation;

import java.io.PrintStream;

public interface Instrumenter {

    Instrumenter DO_NOTHING = new Instrumenter() {
        @Override
        public byte[] instrument(String resourceName, byte[] data) {
            return data;
        }

        @Override
        public void dump(PrintStream out) {
            // Do nothing
        }
    };

    byte[] instrument(String resourceName, byte[] data);
    void dump(PrintStream out);
}
