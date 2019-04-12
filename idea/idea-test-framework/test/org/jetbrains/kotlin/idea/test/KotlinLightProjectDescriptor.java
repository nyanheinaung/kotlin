/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.test;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class KotlinLightProjectDescriptor extends LightProjectDescriptor {
    protected KotlinLightProjectDescriptor() {
    }
    
    public static final KotlinLightProjectDescriptor INSTANCE = new KotlinLightProjectDescriptor();
    
    @Override
    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public Sdk getSdk() {
        return PluginTestCaseBase.mockJdk();
    }

    @Override
    public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
        configureModule(module, model);
    }

    public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model) {
    }
}
