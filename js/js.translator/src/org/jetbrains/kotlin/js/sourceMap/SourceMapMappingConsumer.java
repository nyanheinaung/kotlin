/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.sourceMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Supplier;

public interface SourceMapMappingConsumer {
    void newLine();

    void addMapping(
            @NotNull String source, @Nullable Object sourceIdentity, @NotNull Supplier<Reader> sourceSupplier,
            int sourceLine, int sourceColumn
    );

    void addEmptyMapping();
}
