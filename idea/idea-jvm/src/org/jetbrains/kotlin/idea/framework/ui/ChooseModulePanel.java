/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.framework.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.util.Function;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.configuration.ConfigureKotlinInProjectUtilsKt;
import org.jetbrains.kotlin.idea.configuration.KotlinProjectConfigurator;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChooseModulePanel {
    private JPanel contentPane;
    private JRadioButton allModulesWithKtRadioButton;
    private JRadioButton singleModuleRadioButton;
    private JComboBox singleModuleComboBox;
    private HyperlinkLabel allModulesWithKtNames;
    private JRadioButton allModulesRadioButton;

    @NotNull private final Project project;
    @NotNull private final List<Module> modules;
    @NotNull private final List<Module> modulesWithKtFiles;

    public ChooseModulePanel(@NotNull Project project, @NotNull KotlinProjectConfigurator configurator, Collection<Module> excludeModules) {
        this.project = project;
        this.modules = ConfigureKotlinInProjectUtilsKt.getCanBeConfiguredModules(project, configurator);
        this.modulesWithKtFiles = ConfigureKotlinInProjectUtilsKt.getCanBeConfiguredModulesWithKotlinFiles(project, configurator);

        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

        for (Module module : modules) {
            comboBoxModel.addElement(module.getName());
        }

        if (modulesWithKtFiles.isEmpty()) {
            allModulesWithKtRadioButton.setVisible(false);
            allModulesWithKtNames.setVisible(false);
        }

        singleModuleComboBox.setModel(comboBoxModel);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateComponents();
            }
        };
        singleModuleRadioButton.addActionListener(listener);
        allModulesWithKtRadioButton.addActionListener(listener);
        allModulesRadioButton.addActionListener(listener);

        if (modulesWithKtFiles.size() > 2) {
            allModulesWithKtNames.setHtmlText("<html>" + XmlUtil.escape(modulesWithKtFiles.get(0).getName()) + ", " +
                                              XmlUtil.escape(modulesWithKtFiles.get(1).getName()) +
                                              " and <a href=\"#\">" + (modulesWithKtFiles.size() - 2) + " other modules</a>");
            allModulesWithKtNames.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent event) {
                    JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<Module>("Modules with Kotlin Files", modulesWithKtFiles) {
                        @NotNull
                        @Override
                        public String getTextFor(Module value) {
                            return value.getName();
                        }
                    }).showUnderneathOf(allModulesWithKtNames);
                }
            });
        }
        else {
            allModulesWithKtNames.setText(StringUtil.join(modulesWithKtFiles, new Function<Module, String>() {
                @Override
                public String fun(Module module) {
                    return module.getName();
                }
            }, ", "));
        }

        ButtonGroup modulesGroup = new ButtonGroup();
        modulesGroup.add(allModulesRadioButton);
        modulesGroup.add(allModulesWithKtRadioButton);
        modulesGroup.add(singleModuleRadioButton);

        if (allModulesWithKtRadioButton.isVisible()) {
            allModulesWithKtRadioButton.setSelected(true);
        }
        else {
            allModulesRadioButton.setSelected(true);
        }

        updateComponents();
    }

    public JComponent getContentPane() {
        return contentPane;
    }

    private void updateComponents() {
        singleModuleComboBox.setEnabled(singleModuleRadioButton.isSelected());
    }

    public List<Module> getModulesToConfigure() {
        if (allModulesRadioButton.isSelected()) return modules;
        if (allModulesWithKtRadioButton.isSelected()) return modulesWithKtFiles;

        String selectedItem = (String) singleModuleComboBox.getSelectedItem();
        if (selectedItem == null) return Collections.emptyList();

        return Collections.singletonList(ModuleManager.getInstance(project).findModuleByName(selectedItem));
    }
}
