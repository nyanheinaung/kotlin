/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.KotlinValueArgumentStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtValueArgument extends KtElementImplStub<KotlinValueArgumentStub<? extends KtValueArgument>> implements ValueArgument {
    public KtValueArgument(@NotNull ASTNode node) {
        super(node);
    }

    public KtValueArgument(@NotNull KotlinValueArgumentStub<KtValueArgument> stub) {
        super(stub, KtStubElementTypes.VALUE_ARGUMENT);
    }

    protected KtValueArgument(KotlinValueArgumentStub<? extends KtValueArgument> stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitArgument(this, data);
    }

    private static final TokenSet CONSTANT_EXPRESSIONS_TYPES = TokenSet.create(
            KtStubElementTypes.NULL,
            KtStubElementTypes.BOOLEAN_CONSTANT,
            KtStubElementTypes.FLOAT_CONSTANT,
            KtStubElementTypes.CHARACTER_CONSTANT,
            KtStubElementTypes.INTEGER_CONSTANT,

            KtStubElementTypes.REFERENCE_EXPRESSION,
            KtStubElementTypes.DOT_QUALIFIED_EXPRESSION,

            KtStubElementTypes.STRING_TEMPLATE
    );

    @Override
    @Nullable @IfNotParsed
    public KtExpression getArgumentExpression() {
        KotlinPlaceHolderStub<? extends KtValueArgument> stub = getStub();
        if (stub != null) {
            KtExpression[] constantExpressions = stub.getChildrenByType(CONSTANT_EXPRESSIONS_TYPES, KtExpression.EMPTY_ARRAY);
            if (constantExpressions.length != 0) {
                return constantExpressions[0];
            }
        }

        return findChildByClass(KtExpression.class);
    }

    @Override
    @Nullable
    public KtValueArgumentName getArgumentName() {
        return getStubOrPsiChild(KtStubElementTypes.VALUE_ARGUMENT_NAME);
    }

    @Nullable
    public PsiElement getEqualsToken() {
        return findChildByType(KtTokens.EQ);
    }

    @Override
    public boolean isNamed() {
        return getArgumentName() != null;
    }

    @NotNull
    @Override
    public KtElement asElement() {
        return this;
    }

    @Override
    public LeafPsiElement getSpreadElement() {
        KotlinValueArgumentStub stub = getStub();
        if (stub != null && !stub.isSpread()) {
            return null;
        }

        ASTNode node = getNode().findChildByType(KtTokens.MUL);
        return node == null ? null : (LeafPsiElement) node.getPsi();
    }

    public boolean isSpread() {
        KotlinValueArgumentStub stub = getStub();
        if (stub != null) {
            return stub.isSpread();
        }

        return getSpreadElement() != null;
    }

    @Override
    public boolean isExternal() {
        return false;
    }
}
