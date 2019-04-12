/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.state.GenerationState;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.progress.ProgressIndicatorAndCompilationCanceledStatus;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.Collection;

public class KotlinCodegenFacade {

    public static void compileCorrectFiles(
            @NotNull GenerationState state,
            @NotNull CompilationErrorHandler errorHandler
    ) {
        ProgressIndicatorAndCompilationCanceledStatus.checkCanceled();

        state.beforeCompile();

        ProgressIndicatorAndCompilationCanceledStatus.checkCanceled();

        doGenerateFiles(state.getFiles(), state, errorHandler);
    }

    public static void doGenerateFiles(
            @NotNull Collection<KtFile> files,
            @NotNull GenerationState state,
            @NotNull CompilationErrorHandler errorHandler
    ) {
        state.getCodegenFactory().generateModule(state, files, errorHandler);

        CodegenFactory.Companion.doCheckCancelled(state);
        state.getFactory().done();
    }

    public static void generatePackage(
            @NotNull GenerationState state,
            @NotNull FqName packageFqName,
            @NotNull Collection<KtFile> jetFiles,
            @NotNull CompilationErrorHandler errorHandler
    ) {
        DefaultCodegenFactory.INSTANCE.generatePackage(state, packageFqName, jetFiles, errorHandler);
    }

    private KotlinCodegenFacade() {}
}
