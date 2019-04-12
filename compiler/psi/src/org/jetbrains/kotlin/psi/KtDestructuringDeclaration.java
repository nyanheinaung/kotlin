/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.lexer.KtTokens;

import java.util.List;

import static org.jetbrains.kotlin.lexer.KtTokens.*;

public class KtDestructuringDeclaration extends KtDeclarationImpl implements KtValVarKeywordOwner, KtDeclarationWithInitializer {
    public KtDestructuringDeclaration(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitDestructuringDeclaration(this, data);
    }

    @NotNull
    public List<KtDestructuringDeclarationEntry> getEntries() {
        return findChildrenByType(KtNodeTypes.DESTRUCTURING_DECLARATION_ENTRY);
    }

    @Nullable
    @Override
    public KtExpression getInitializer() {
        ASTNode eqNode = getNode().findChildByType(EQ);
        if (eqNode == null) {
            return null;
        }
        return PsiTreeUtil.getNextSiblingOfType(eqNode.getPsi(), KtExpression.class);
    }

    @Override
    public boolean hasInitializer() {
        return getInitializer() != null;
    }

    public boolean isVar() {
        return getNode().findChildByType(KtTokens.VAR_KEYWORD) != null;
    }

    @Override
    @Nullable
    public PsiElement getValOrVarKeyword() {
        return findChildByType(TokenSet.create(VAL_KEYWORD, VAR_KEYWORD));
    }

    @Nullable
    public PsiElement getRPar() {
        return findChildByType(KtTokens.RPAR);
    }

    @Nullable
    public PsiElement getLPar() {
        return findChildByType(KtTokens.LPAR);
    }
}
