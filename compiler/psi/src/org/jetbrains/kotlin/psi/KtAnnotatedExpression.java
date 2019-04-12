/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.psi.psiUtil.KtPsiUtilKt;

import java.util.List;

public class KtAnnotatedExpression extends KtExpressionImpl implements KtAnnotated, KtAnnotationsContainer {
    public KtAnnotatedExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitAnnotatedExpression(this, data);
    }

    @Nullable
    public KtExpression getBaseExpression() {
        return findChildByClass(KtExpression.class);
    }

    @Override
    @NotNull
    public List<KtAnnotation> getAnnotations() {
        return findChildrenByType(KtNodeTypes.ANNOTATION);
    }

    @Override
    @NotNull
    public List<KtAnnotationEntry> getAnnotationEntries() {
        return KtPsiUtilKt.collectAnnotationEntriesFromStubOrPsi(this);
    }
}
