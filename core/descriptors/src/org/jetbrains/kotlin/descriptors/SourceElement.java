/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;

public interface SourceElement {
    SourceElement NO_SOURCE = new SourceElement() {
        @Override
        public String toString() {
            return "NO_SOURCE";
        }

        @NotNull
        @Override
        public SourceFile getContainingFile() {
            return SourceFile.NO_SOURCE_FILE;
        }
    };

    @NotNull
    SourceFile getContainingFile();
}
