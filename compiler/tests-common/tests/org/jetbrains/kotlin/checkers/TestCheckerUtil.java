/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers;

import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.test.KotlinTestUtils;


public class TestCheckerUtil {
    @NotNull
    public static KtFile createCheckAndReturnPsiFile(@NotNull String fileName, @NotNull String text, @NotNull Project project) {
        KtFile myFile = KotlinTestUtils.createFile(fileName, text, project);
        ensureParsed(myFile);
        TestCase.assertEquals("light virtual file text mismatch", text, ((LightVirtualFile) myFile.getVirtualFile()).getContent().toString());
        TestCase.assertEquals("virtual file text mismatch", text, LoadTextUtil.loadText(myFile.getVirtualFile()));
        //noinspection ConstantConditions
        TestCase.assertEquals("doc text mismatch", text, myFile.getViewProvider().getDocument().getText());
        TestCase.assertEquals("psi text mismatch", text, myFile.getText());
        return myFile;
    }

    private static void ensureParsed(PsiFile file) {
        file.accept(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                element.acceptChildren(this);
            }
        });
    }
}
