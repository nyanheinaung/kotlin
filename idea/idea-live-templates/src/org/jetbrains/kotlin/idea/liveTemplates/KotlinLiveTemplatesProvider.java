/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.liveTemplates;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;

public class KotlinLiveTemplatesProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[]{"liveTemplates/Kotlin"};
    }

    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return new String[0];
    }
}
