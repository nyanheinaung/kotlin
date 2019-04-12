/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.psi.KtFile;

public class KotlinParser implements PsiParser {


    public KotlinParser(Project project) {
    }

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType iElementType, @NotNull PsiBuilder psiBuilder) {
        throw new IllegalStateException("use another parse");
    }

    // we need this method because we need psiFile
    @NotNull
    public ASTNode parse(IElementType iElementType, PsiBuilder psiBuilder, PsiFile psiFile) {
        KotlinParsing ktParsing = KotlinParsing.createForTopLevel(new SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder));
        String extension = FileUtilRt.getExtension(psiFile.getName());
        if (extension.isEmpty() || extension.equals(KotlinFileType.EXTENSION) || (psiFile instanceof KtFile && ((KtFile) psiFile).isCompiled())) {
            ktParsing.parseFile();
        }
        else {
            ktParsing.parseScript();
        }
        return psiBuilder.getTreeBuilt();
    }

    @NotNull
    public static ASTNode parseTypeCodeFragment(PsiBuilder psiBuilder) {
        KotlinParsing ktParsing = KotlinParsing.createForTopLevel(new SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder));
        ktParsing.parseTypeCodeFragment();
        return psiBuilder.getTreeBuilt();
    }

    @NotNull
    public static ASTNode parseExpressionCodeFragment(PsiBuilder psiBuilder) {
        KotlinParsing ktParsing = KotlinParsing.createForTopLevel(new SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder));
        ktParsing.parseExpressionCodeFragment();
        return psiBuilder.getTreeBuilt();
    }

    @NotNull
    public static ASTNode parseBlockCodeFragment(PsiBuilder psiBuilder) {
        KotlinParsing ktParsing = KotlinParsing.createForTopLevel(new SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder));
        ktParsing.parseBlockCodeFragment();
        return psiBuilder.getTreeBuilt();
    }

    @NotNull
    public static ASTNode parseLambdaExpression(PsiBuilder psiBuilder) {
        KotlinParsing ktParsing = KotlinParsing.createForTopLevel(new SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder));
        ktParsing.parseLambdaExpression();
        return psiBuilder.getTreeBuilt();
    }
}
