/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.findUsages.dialogs;

import com.intellij.find.FindBundle;
import com.intellij.find.FindSettings;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.JavaFindUsagesDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.StateRestoringCheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinBundle;
import org.jetbrains.kotlin.idea.findUsages.KotlinPropertyFindUsagesOptions;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.KtNamedDeclaration;
import org.jetbrains.kotlin.psi.psiUtil.PsiUtilsKt;

import javax.swing.*;

public class KotlinFindPropertyUsagesDialog extends JavaFindUsagesDialog<KotlinPropertyFindUsagesOptions> {
    public KotlinFindPropertyUsagesDialog(
            PsiElement element,
            Project project,
            KotlinPropertyFindUsagesOptions findUsagesOptions,
            boolean toShowInNewTab,
            boolean mustOpenInNewTab,
            boolean isSingleFile,
            FindUsagesHandler handler
    ) {
        super(element, project, findUsagesOptions, toShowInNewTab, mustOpenInNewTab, isSingleFile, handler);
    }

    private StateRestoringCheckBox readAccesses;
    private StateRestoringCheckBox writeAccesses;
    private StateRestoringCheckBox overrideUsages;
    private StateRestoringCheckBox expectedUsages;

    @NotNull
    @Override
    protected KotlinPropertyFindUsagesOptions getFindUsagesOptions() {
        return (KotlinPropertyFindUsagesOptions) myFindUsagesOptions;
    }

    @Override
    public JComponent getPreferredFocusedControl() {
        return myCbToSkipResultsWhenOneUsage;
    }

    @Override
    public void calcFindUsagesOptions(KotlinPropertyFindUsagesOptions options) {
        super.calcFindUsagesOptions(options);

        options.isReadAccess = isSelected(readAccesses);
        options.isWriteAccess = isSelected(writeAccesses);
        options.setSearchOverrides(isSelected(overrideUsages));
        if (expectedUsages != null) {
            options.setSearchExpected(expectedUsages.isSelected());
        }
    }

    @Override
    protected JPanel createFindWhatPanel() {
        JPanel findWhatPanel = new JPanel();
        findWhatPanel.setBorder(IdeBorderFactory.createTitledBorder(FindBundle.message("find.what.group"), true));
        findWhatPanel.setLayout(new BoxLayout(findWhatPanel, BoxLayout.Y_AXIS));

        KotlinPropertyFindUsagesOptions options = getFindUsagesOptions();

        readAccesses = addCheckboxToPanel(
                KotlinBundle.message("find.what.property.readers.checkbox"),
                options.isReadAccess,
                findWhatPanel,
                true
        );
        writeAccesses = addCheckboxToPanel(
                KotlinBundle.message("find.what.property.writers.checkbox"),
                options.isWriteAccess,
                findWhatPanel,
                true
        );

        return findWhatPanel;
    }

    @Override
    public void configureLabelComponent(@NotNull SimpleColoredComponent coloredComponent) {
        Utils.configureLabelComponent(coloredComponent, (KtNamedDeclaration) getPsiElement());
    }

    @Override
    protected void addUsagesOptions(JPanel optionsPanel) {
        super.addUsagesOptions(optionsPanel);

        KtNamedDeclaration property = (KtNamedDeclaration) getPsiElement();

        boolean isAbstract = property.hasModifier(KtTokens.ABSTRACT_KEYWORD);
        boolean isOpen = property.hasModifier(KtTokens.OPEN_KEYWORD);
        if (isOpen || isAbstract) {
            overrideUsages = addCheckboxToPanel(
                    isAbstract
                    ? KotlinBundle.message("find.what.implementing.properties.checkbox")
                    : KotlinBundle.message("find.what.overriding.properties.checkbox"),
                    FindSettings.getInstance().isSearchOverloadedMethods(),
                    optionsPanel,
                    false
            );
        }
        boolean isActual = PsiUtilsKt.hasActualModifier(property);
        KotlinPropertyFindUsagesOptions options = getFindUsagesOptions();
        if (isActual) {
            expectedUsages = addCheckboxToPanel(
                    "Expected properties",
                    options.getSearchExpected(),
                    optionsPanel,
                    false
            );
        }
    }

    @Override
    protected void update() {
        setOKActionEnabled(isSelected(readAccesses) || isSelected(writeAccesses));
    }
}
