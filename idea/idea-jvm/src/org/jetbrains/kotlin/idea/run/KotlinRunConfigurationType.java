/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.run;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinIcons;
import org.jetbrains.kotlin.idea.KotlinLanguage;

public class KotlinRunConfigurationType extends ConfigurationTypeBase {
    public static KotlinRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(KotlinRunConfigurationType.class);
    }

    public KotlinRunConfigurationType() {
        super("JetRunConfigurationType", KotlinLanguage.NAME, KotlinLanguage.NAME, KotlinIcons.SMALL_LOGO);
        addFactory(new KotlinRunConfigurationFactory(this));
    }

    private static class KotlinRunConfigurationFactory extends ConfigurationFactory {
        protected KotlinRunConfigurationFactory(@NotNull ConfigurationType type) {
            super(type);
        }

        @NotNull
        @Override
        public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new KotlinRunConfiguration("", new JavaRunConfigurationModule(project, true), this);
        }
    }
}
