/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests;

import org.jetbrains.kotlin.jps.build.BaseKotlinJpsBuildTestCase;
import org.junit.Ignore;

import java.io.File;
import java.io.IOException;

@Ignore
public class AndroidJpsBuildTestCase extends BaseKotlinJpsBuildTestCase {
    private static final String PROJECT_NAME = "android-module";
    private static final String SDK_NAME = "Android_SDK";

    private final File workDir = new File(AndroidRunner.getPathManager().getTmpFolder());

    public void doTest() {
        initProject();
        rebuildAllModules();
        buildAllModules().assertSuccessful();
    }

    @Override
    protected String getProjectName() {
        return "android-module";
    }

    @Override
    protected void runTest() throws Throwable {
        doTest();
    }

    @Override
    public String getName() {
        return "AndroidJpsTest";
    }

    @Override
    protected File doGetProjectDir() throws IOException {
        return workDir;
    }

    private void initProject() {
        addJdk(SDK_NAME, AndroidRunner.getPathManager().getPlatformFolderInAndroidSdk() + "/android.jar");
        loadProject(workDir.getAbsolutePath() + File.separator + PROJECT_NAME + ".ipr");
    }
}
