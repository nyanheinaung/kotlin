/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.surroundWith.expression;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.codeInsight.CodeInsightUtils;
import org.jetbrains.kotlin.psi.KtExpression;

public class KotlinExpressionSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
            new KotlinNotSurrounder(),
            new KotlinStringTemplateSurrounder(),
            new KotlinParenthesesSurrounder(),
            new KotlinWhenSurrounder(),
            new KotlinWithIfExpressionSurrounder(/* withElse = */false),
            new KotlinWithIfExpressionSurrounder(/* withElse = */true),
            new KotlinTryExpressionSurrounder.TryCatch(),
            new KotlinTryExpressionSurrounder.TryCatchFinally(),
            new KotlinIfElseExpressionSurrounder(/* withBraces = */false),
            new KotlinIfElseExpressionSurrounder(/* withBraces = */true)
    };

    @Override
    @NotNull
    public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        KtExpression expression = (KtExpression) CodeInsightUtils.findElement(
                file, startOffset, endOffset, CodeInsightUtils.ElementKind.EXPRESSION);

        return expression == null ? PsiElement.EMPTY_ARRAY : new PsiElement[] {expression};
    }

    @Override
    @NotNull
    public Surrounder[] getSurrounders() {
        return SURROUNDERS;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }
}
