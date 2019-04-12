/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.kdoc.psi.api.KDoc;
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken;
import org.jetbrains.kotlin.psi.addRemoveModifier.AddRemoveModifierKt;
import org.jetbrains.kotlin.psi.findDocComment.FindDocCommentKt;

import java.util.Collections;
import java.util.List;

public abstract class KtDeclarationImpl extends KtExpressionImpl implements KtDeclaration {
    public KtDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public KtModifierList getModifierList() {
        return (KtModifierList) findChildByType(KtNodeTypes.MODIFIER_LIST);
    }

    @Override
    public boolean hasModifier(@NotNull KtModifierKeywordToken modifier) {
        KtModifierList modifierList = getModifierList();
        return modifierList != null && modifierList.hasModifier(modifier);
    }

    @Override
    public void addModifier(@NotNull KtModifierKeywordToken modifier) {
        AddRemoveModifierKt.addModifier(this, modifier);
    }

    @Override
    public void removeModifier(@NotNull KtModifierKeywordToken modifier) {
        AddRemoveModifierKt.removeModifier(this, modifier);
    }

    @NotNull
    @Override
    public KtAnnotationEntry addAnnotationEntry(@NotNull KtAnnotationEntry annotationEntry) {
        return AddRemoveModifierKt.addAnnotationEntry(this, annotationEntry);
    }

    @NotNull
    @Override
    public List<KtAnnotationEntry> getAnnotationEntries() {
        KtModifierList modifierList = getModifierList();
        if (modifierList == null) return Collections.emptyList();
        return modifierList.getAnnotationEntries();
    }

    @NotNull
    @Override
    public List<KtAnnotation> getAnnotations() {
        KtModifierList modifierList = getModifierList();
        if (modifierList == null) return Collections.emptyList();
        return modifierList.getAnnotations();
    }

    @Nullable
    @Override
    public KDoc getDocComment() {
        return FindDocCommentKt.findDocComment(this);
    }
}
