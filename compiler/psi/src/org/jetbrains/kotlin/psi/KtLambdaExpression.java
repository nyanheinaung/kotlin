/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifiableCodeBlock;
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.lexer.KtTokens;

import java.util.List;

public class KtLambdaExpression extends LazyParseablePsiElement implements KtExpression, PsiModifiableCodeBlock {
    public KtLambdaExpression(CharSequence text) {
        super(KtNodeTypes.LAMBDA_EXPRESSION, text);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitLambdaExpression(this, data);
    }

    @NotNull
    public KtFunctionLiteral getFunctionLiteral() {
        return findChildByType(KtNodeTypes.FUNCTION_LITERAL).getPsi(KtFunctionLiteral.class);
    }

    @NotNull
    public List<KtParameter> getValueParameters() {
        return getFunctionLiteral().getValueParameters();
    }

    @Nullable
    public KtBlockExpression getBodyExpression() {
        return getFunctionLiteral().getBodyExpression();
    }

    public boolean hasDeclaredReturnType() {
        return getFunctionLiteral().getTypeReference() != null;
    }

    @NotNull
    public KtElement asElement() {
        return this;
    }

    @NotNull
    public ASTNode getLeftCurlyBrace() {
        return getFunctionLiteral().getNode().findChildByType(KtTokens.LBRACE);
    }

    @Nullable
    public ASTNode getRightCurlyBrace() {
        return getFunctionLiteral().getNode().findChildByType(KtTokens.RBRACE);
    }

    @NotNull
    @Override
    public KtFile getContainingKtFile() {
        PsiFile file = getContainingFile();
        if(!(file instanceof KtFile))  {
            String fileString = (file != null && file.isValid()) ? file.getText() : "";
            throw new IllegalStateException("KtElement not inside KtFile: " + file + fileString +
                                            "for element " + this + " of type " + this.getClass() + " node = " + getNode());
        }
        return (KtFile) file;
    }

    @Override
    public <D> void acceptChildren(@NotNull KtVisitor<Void, D> visitor, D data) {
        KtPsiUtil.visitChildren(this, visitor, data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof KtVisitor) {
            accept((KtVisitor) visitor, null);
        }
        else {
            visitor.visitElement(this);
        }
    }

    @Override
    public String toString() {
        return getNode().getElementType().toString();
    }

    @NotNull
    @Override
    public KtElement getPsiOrParent() {
        return this;
    }

    @Override
    public boolean shouldChangeModificationCount(PsiElement place) {
        return false;
    }
}
