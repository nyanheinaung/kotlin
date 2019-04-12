/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.findUsages.dialogs;

import com.intellij.ui.SimpleColoredComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.idea.search.usagesSearch.UtilsKt;
import org.jetbrains.kotlin.psi.KtNamedDeclaration;
import org.jetbrains.kotlin.renderer.DescriptorRenderer;

import javax.swing.*;
import java.awt.*;

class Utils {
    private Utils() {
    }

    public static void configureLabelComponent(
            @NotNull SimpleColoredComponent coloredComponent,
            @NotNull KtNamedDeclaration declaration
    ) {
        DeclarationDescriptor descriptor = UtilsKt.getDescriptor(declaration);
        if (descriptor != null) {
            coloredComponent.append(DescriptorRenderer.COMPACT.render(descriptor));
        }
    }

    static boolean renameCheckbox(@NotNull JPanel panel, @NotNull String srcText, @NotNull String destText) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.getText().equals(srcText)) {
                    checkBox.setText(destText);
                    return true;
                }
            }
        }

        return false;
    }

    static void removeCheckbox(@NotNull JPanel panel, @NotNull String srcText) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.getText().equals(srcText)) {
                    panel.remove(checkBox);
                    return;
                }
            }
        }
    }
}
