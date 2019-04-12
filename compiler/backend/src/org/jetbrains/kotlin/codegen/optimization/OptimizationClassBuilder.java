/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.codegen.ClassBuilder;
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder;
import org.jetbrains.kotlin.codegen.state.GenerationState;
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin;
import org.jetbrains.org.objectweb.asm.MethodVisitor;

public class OptimizationClassBuilder extends DelegatingClassBuilder {
    private final ClassBuilder delegate;
    private final GenerationState generationState;

    public OptimizationClassBuilder(@NotNull ClassBuilder delegate, @NotNull GenerationState generationState) {
        this.delegate = delegate;
        this.generationState = generationState;
    }

    @NotNull
    @Override
    public ClassBuilder getDelegate() {
        return delegate;
    }

    @NotNull
    @Override
    public MethodVisitor newMethod(
            @NotNull JvmDeclarationOrigin origin,
            int access,
            @NotNull String name,
            @NotNull String desc,
            @Nullable String signature,
            @Nullable String[] exceptions
    ) {
        return new OptimizationMethodVisitor(
                super.newMethod(origin, access, name, desc, signature, exceptions),
                generationState, access, name, desc, signature, exceptions
        );
    }
}
