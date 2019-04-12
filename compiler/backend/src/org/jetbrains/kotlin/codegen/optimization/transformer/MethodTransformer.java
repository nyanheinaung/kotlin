/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.transformer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.org.objectweb.asm.tree.MethodNode;
import org.jetbrains.org.objectweb.asm.tree.analysis.*;

public abstract class MethodTransformer {
    @NotNull
    protected static <V extends Value> Frame<V>[] runAnalyzer(
            @NotNull Analyzer<V> analyzer,
            @NotNull String internalClassName,
            @NotNull MethodNode node
    ) {
        try {
            return analyzer.analyze(internalClassName, node);
        }
        catch (AnalyzerException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static <V extends Value> Frame<V>[] analyze(
            @NotNull String internalClassName,
            @NotNull MethodNode node,
            @NotNull Interpreter<V> interpreter
    ) {
        return runAnalyzer(new Analyzer<>(interpreter), internalClassName, node);
    }

    public abstract void transform(@NotNull String internalClassName, @NotNull MethodNode methodNode);
}
