/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger;


import com.intellij.openapi.options.ConfigurableUi;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class KotlinDelegatedPropertyRendererConfigurableUi implements ConfigurableUi<KotlinDebuggerSettings> {
    private JCheckBox renderDelegatedProperties;
    private JPanel myPanel;

    @Override
    public void reset(@NotNull KotlinDebuggerSettings settings) {
        boolean flag = settings.getDEBUG_RENDER_DELEGATED_PROPERTIES();
        renderDelegatedProperties.setSelected(flag);
    }

    @Override
    public boolean isModified(@NotNull KotlinDebuggerSettings settings) {
        return settings.getDEBUG_RENDER_DELEGATED_PROPERTIES() != renderDelegatedProperties.isSelected();
    }

    @Override
    public void apply(@NotNull KotlinDebuggerSettings settings) {
        settings.setDEBUG_RENDER_DELEGATED_PROPERTIES(renderDelegatedProperties.isSelected());
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return myPanel;
    }
}
