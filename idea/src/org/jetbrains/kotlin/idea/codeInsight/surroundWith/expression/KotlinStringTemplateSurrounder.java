/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.surroundWith.expression;

import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.KotlinBundle;
import org.jetbrains.kotlin.psi.*;

public class KotlinStringTemplateSurrounder extends KotlinExpressionSurrounder {
    @Override
    public String getTemplateDescription() {
        return KotlinBundle.message("surround.with.string.template");
    }

    @Override
    public boolean isApplicable(@NotNull KtExpression expression) {
        return !(expression instanceof KtStringTemplateExpression) && super.isApplicable(expression);
    }

    @Nullable
    @Override
    public TextRange surroundExpression(@NotNull Project project, @NotNull Editor editor, @NotNull KtExpression expression) {
        KtStringTemplateExpression stringTemplateExpression = (KtStringTemplateExpression) KtPsiFactoryKt.KtPsiFactory(expression).createExpression(
                getCodeTemplate(expression)
        );
        KtStringTemplateEntry templateEntry = stringTemplateExpression.getEntries()[0];
        KtExpression innerExpression = templateEntry.getExpression();
        assert innerExpression != null : "JetExpression should exists for " + stringTemplateExpression.toString();
        innerExpression.replace(expression);

        expression = (KtExpression) expression.replace(stringTemplateExpression);

        CodeInsightUtilBase.forcePsiPostprocessAndRestoreElement(expression);

        int offset = expression.getTextRange().getEndOffset();
        return new TextRange(offset, offset);
    }

    private String getCodeTemplate(KtExpression expression) {
        if (expression.getChildren().length > 0 ||
            expression instanceof KtConstantExpression) {
            return "\"${a}\"";
        }
        return "\"$a\"";
    }
}
