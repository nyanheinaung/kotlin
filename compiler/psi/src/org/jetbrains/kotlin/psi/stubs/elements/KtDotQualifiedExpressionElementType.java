/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression;

public class KtDotQualifiedExpressionElementType extends KtPlaceHolderStubElementType<KtDotQualifiedExpression> {
    public KtDotQualifiedExpressionElementType(@NotNull @NonNls String debugName) {
        super(debugName, KtDotQualifiedExpression.class);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        ASTNode treeParent = node.getTreeParent();
        if (treeParent == null) return false;

        IElementType parentElementType = treeParent.getElementType();
        if (parentElementType == KtStubElementTypes.IMPORT_DIRECTIVE ||
            parentElementType == KtStubElementTypes.PACKAGE_DIRECTIVE ||
            parentElementType == KtStubElementTypes.VALUE_ARGUMENT ||
            parentElementType == KtStubElementTypes.DOT_QUALIFIED_EXPRESSION
        ) {
            return super.shouldCreateStub(node);
        }

        return false;
    }
}
