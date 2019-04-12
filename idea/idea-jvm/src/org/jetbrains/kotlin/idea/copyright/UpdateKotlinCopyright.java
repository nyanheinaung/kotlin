/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.copyright;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.maddyhome.idea.copyright.CopyrightProfile;
import com.maddyhome.idea.copyright.psi.UpdatePsiFileCopyright;
import org.jetbrains.kotlin.psi.KtImportList;

class UpdateKotlinCopyright extends UpdatePsiFileCopyright {

    UpdateKotlinCopyright(Project project, Module module, VirtualFile root, CopyrightProfile copyrightProfile) {
        super(project, module, root, copyrightProfile);
    }

    @Override
    protected void scanFile() {
        PsiElement first = getFile().getFirstChild();
        PsiElement last = first;
        PsiElement next = first;
        while (next != null) {
            if (next instanceof PsiComment || next instanceof PsiWhiteSpace || next.getText().isEmpty()) {
                next = getNextSibling(next);
            }
            else {
                break;
            }
            last = next;
        }

        if (first != null) {
            checkComments(first, last, true);
        }
    }
}
