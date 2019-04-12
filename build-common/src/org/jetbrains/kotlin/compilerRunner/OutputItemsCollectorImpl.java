/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compilerRunner;

import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class OutputItemsCollectorImpl implements OutputItemsCollector {
    private final List<SimpleOutputItem> outputs = ContainerUtil.newArrayList();

    @Override
    public void add(Collection<File> sourceFiles, File outputFile) {
        outputs.add(new SimpleOutputItem(sourceFiles, outputFile));
    }

    @NotNull
    public List<SimpleOutputItem> getOutputs() {
        return outputs;
    }
}
