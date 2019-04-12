/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.idea.core.formatter.KotlinCodeStyleSettings;

public class KotlinCodeStyleSettingsProvider extends CodeStyleSettingsProviderCompat {

    @Override
    public String getConfigurableDisplayName() {
        return KotlinLanguage.NAME;
    }

    @Override
    public Language getLanguage() {
        return KotlinLanguage.INSTANCE;
    }

    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new KotlinCodeStyleSettings(settings);
    }

    @NotNull
    @Override
    public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings modelSettings) {
        return new CodeStyleAbstractConfigurable(settings, modelSettings, KotlinLanguage.NAME) {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                return new TabbedLanguageCodeStylePanel(KotlinLanguage.INSTANCE, getCurrentSettings(), settings) {
                    @Override
                    protected void initTabs(CodeStyleSettings settings) {
                        // TODO: activate all parent tabs
                        addIndentOptionsTab(settings);
                        addSpacesTab(settings);
                        addWrappingAndBracesTab(settings);
                        addBlankLinesTab(settings);
                        addTab(new ImportSettingsPanelWrapper(settings));

                        // BUNCH: 182
                        //noinspection IncompatibleAPI
                        for (CodeStyleSettingsProvider provider : CodeStyleSettingsProvider.EXTENSION_POINT_NAME.getExtensions()) {
                            if (provider.getLanguage() == KotlinLanguage.INSTANCE && !provider.hasSettingsPage()) {
                                createTab(provider);
                            }
                        }

                        addTab(new KotlinSaveStylePanel(settings));
                    }
                };
            }

            @Override
            public String getHelpTopic() {
                return null;
            }
        };
    }
}
