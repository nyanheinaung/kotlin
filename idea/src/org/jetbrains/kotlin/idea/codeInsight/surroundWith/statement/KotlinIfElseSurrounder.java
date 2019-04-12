/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.surroundWith.statement;


import com.intellij.codeInsight.CodeInsightBundle;
import org.jetbrains.annotations.NotNull;

public class KotlinIfElseSurrounder extends KotlinIfSurrounderBase {

    @Override
    public String getTemplateDescription() {
        return CodeInsightBundle.message("surround.with.ifelse.template");
    }

    @NotNull
    @Override
    protected String getCodeTemplate() {
        return "if (a) { \n} else { \n}";
    }

    @Override
    protected boolean isGenerateDefaultInitializers() {
        return false;
    }
}
