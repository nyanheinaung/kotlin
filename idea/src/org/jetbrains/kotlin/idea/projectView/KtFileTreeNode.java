/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.projectView;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KtFileTreeNode extends PsiFileNode {
    public KtFileTreeNode(Project project, KtFile value, ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    public final KtFile getKtFile() {
        return (KtFile) getValue();
    }

    @Override
    public Collection<AbstractTreeNode> getChildrenImpl() {
        KtFile file = (KtFile) getValue();

        if (file == null) return Collections.emptyList();
        ArrayList<AbstractTreeNode> result = new ArrayList<AbstractTreeNode>();

        if (getSettings().isShowMembers()) {
            @SuppressWarnings("ConstantConditions") List<KtDeclaration> declarations = (file.isScript() ? file.getScript() : file).getDeclarations();

            for (KtDeclaration declaration : declarations) {
                if (declaration instanceof KtClassOrObject) {
                    result.add(new KtClassOrObjectTreeNode(file.getProject(), (KtClassOrObject) declaration, getSettings()));
                }
                else if (getSettings().isShowMembers()) {
                    result.add(new KtDeclarationTreeNode(getProject(), declaration, getSettings()));
                }
            }
        }

        return result;
    }
}
