/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;

public class KotlinActionGroup extends DefaultActionGroup implements DumbAware {
    @Override
    public void update(AnActionEvent event) {
        Presentation p = event.getPresentation();
        boolean hasProject = event.getData(CommonDataKeys.PROJECT) != null;

        p.setVisible(hasProject);
    }
}
