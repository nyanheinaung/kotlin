/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.ClassBuilderFactory;
import org.jetbrains.kotlin.codegen.DelegatingClassBuilderFactory;
import org.jetbrains.kotlin.codegen.state.GenerationState;
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin;

public class OptimizationClassBuilderFactory extends DelegatingClassBuilderFactory {
    private final GenerationState generationState;

    public OptimizationClassBuilderFactory(ClassBuilderFactory delegate, @NotNull GenerationState generationState) {
        super(delegate);
        this.generationState = generationState;
    }

    @NotNull
    @Override
    public OptimizationClassBuilder newClassBuilder(@NotNull JvmDeclarationOrigin origin) {
        return new OptimizationClassBuilder(getDelegate().newClassBuilder(origin), generationState);
    }
}
