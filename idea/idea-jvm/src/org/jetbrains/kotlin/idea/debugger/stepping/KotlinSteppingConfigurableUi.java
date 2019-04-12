/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.stepping;


import com.intellij.openapi.options.ConfigurableUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.debugger.KotlinDebuggerSettings;

import javax.swing.*;

public class KotlinSteppingConfigurableUi implements ConfigurableUi<KotlinDebuggerSettings> {
    private JCheckBox ignoreKotlinMethods;
    private JPanel myPanel;

    @Override
    public void reset(@NotNull KotlinDebuggerSettings settings) {
        boolean flag = settings.getDEBUG_DISABLE_KOTLIN_INTERNAL_CLASSES();
        ignoreKotlinMethods.setSelected(flag);
    }

    @Override
    public boolean isModified(@NotNull KotlinDebuggerSettings settings) {
        return settings.getDEBUG_DISABLE_KOTLIN_INTERNAL_CLASSES() != ignoreKotlinMethods.isSelected();
    }

    @Override
    public void apply(@NotNull KotlinDebuggerSettings settings) {
        settings.setDEBUG_DISABLE_KOTLIN_INTERNAL_CLASSES(ignoreKotlinMethods.isSelected());
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return myPanel;
    }
}
