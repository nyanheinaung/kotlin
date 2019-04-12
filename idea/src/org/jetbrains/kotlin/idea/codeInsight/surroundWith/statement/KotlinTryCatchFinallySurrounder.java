/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.surroundWith.statement;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtTryExpression;

public class KotlinTryCatchFinallySurrounder extends KotlinTrySurrounderBase {

    @Override
    protected String getCodeTemplate() {
        return "try { \n} catch(e: Exception) {\n} finally {\n}";
    }

    @NotNull
    @Override
    protected TextRange getTextRangeForCaret(@NotNull KtTryExpression expression) {
        return getCatchTypeParameterTextRange(expression);
    }

    @Override
    public String getTemplateDescription() {
        return CodeInsightBundle.message("surround.with.try.catch.finally.template");
    }
}
