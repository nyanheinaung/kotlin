/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Diagnostic {

    @NotNull
    DiagnosticFactory<?> getFactory();

    @NotNull
    Severity getSeverity();

    @NotNull
    PsiElement getPsiElement();

    @NotNull
    List<TextRange> getTextRanges();

    @NotNull
    PsiFile getPsiFile();

    boolean isValid();
}
