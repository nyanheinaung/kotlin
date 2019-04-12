/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.hierarchy.calls;

import com.intellij.ide.hierarchy.CallHierarchyBrowserBase;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.hierarchy.JavaHierarchyUtil;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.ui.PopupHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.hierarchy.HierarchyUtilsKt;
import org.jetbrains.kotlin.psi.KtElement;

import javax.swing.*;
import java.util.Comparator;
import java.util.Map;

public class KotlinCallHierarchyBrowser extends CallHierarchyBrowserBase {
    public KotlinCallHierarchyBrowser(@NotNull PsiElement element) {
        super(element.getProject(), element);
    }

    @Override
    protected void createTrees(@NotNull Map<String, JTree> type2TreeMap) {
        ActionGroup group = (ActionGroup) ActionManager.getInstance().getAction(IdeActions.GROUP_CALL_HIERARCHY_POPUP);

        JTree tree1 = createTree(false);
        PopupHandler.installPopupHandler(tree1, group, ActionPlaces.CALL_HIERARCHY_VIEW_POPUP, ActionManager.getInstance());
        BaseOnThisMethodAction baseOnThisMethodAction = new BaseOnThisMethodAction();
        baseOnThisMethodAction.registerCustomShortcutSet(
                ActionManager.getInstance().getAction(IdeActions.ACTION_CALL_HIERARCHY).getShortcutSet(), tree1
        );
        type2TreeMap.put(CALLEE_TYPE, tree1);

        JTree tree2 = createTree(false);
        PopupHandler.installPopupHandler(tree2, group, ActionPlaces.CALL_HIERARCHY_VIEW_POPUP, ActionManager.getInstance());
        baseOnThisMethodAction.registerCustomShortcutSet(
                ActionManager.getInstance().getAction(IdeActions.ACTION_CALL_HIERARCHY).getShortcutSet(), tree2
        );
        type2TreeMap.put(CALLER_TYPE, tree2);
    }

    private static PsiElement getTargetElement(@NotNull HierarchyNodeDescriptor descriptor) {
        if (descriptor instanceof KotlinCallHierarchyNodeDescriptor) {
            return descriptor.getPsiElement();
        }
        return null;
    }

    @Override
    protected PsiElement getElementFromDescriptor(@NotNull HierarchyNodeDescriptor descriptor) {
        return getTargetElement(descriptor);
    }

    @Override
    protected boolean isApplicableElement(@NotNull PsiElement element) {
        if (element instanceof PsiClass) return false; // PsiClass is not allowed at the hierarchy root
        return CallHierarchyUtilsKt.isCallHierarchyElement(element);
    }

    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String typeName, @NotNull PsiElement psiElement) {
        if (!(psiElement instanceof KtElement)) return null;

        if (typeName.equals(CALLER_TYPE)) {
            return new KotlinCallerTreeStructure((KtElement) psiElement, getCurrentScopeType());
        }

        if (typeName.equals(CALLEE_TYPE)) {
            return new KotlinCalleeTreeStructure((KtElement) psiElement, getCurrentScopeType());
        }

        return null;
    }

    @Override
    protected Comparator<NodeDescriptor> getComparator() {
        return JavaHierarchyUtil.getComparator(myProject);
    }
}
