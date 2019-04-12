/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.debugger.filter.DebuggerFiltersUtilKt;

public class JvmPluginStartupComponent implements ApplicationComponent {
    public static JvmPluginStartupComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(JvmPluginStartupComponent.class);
    }

    @Override
    @NotNull
    public String getComponentName() {
        return JvmPluginStartupComponent.class.getName();
    }

    @Override
    public void initComponent() {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            ThreadTrackerPatcherForTeamCityTesting.INSTANCE.patchThreadTracker();
        }

        DebuggerFiltersUtilKt.addKotlinStdlibDebugFilterIfNeeded();
    }

    @Override
    public void disposeComponent() {}
}
