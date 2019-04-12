/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests;

import org.jetbrains.kotlin.android.tests.download.SDKDownloader;

import java.io.File;

public class PathManager {

    private final String tmpFolder;
    private final String rootFolder;

    public PathManager(String rootFolder, String tmpFolder) {
        this.tmpFolder = tmpFolder;
        this.rootFolder = rootFolder;
    }

    public String getPlatformFolderInAndroidSdk() {
        return getAndroidSdkRoot() + "/platforms";
    }

    public String getAndroidEmulatorRoot() {
        String androidEmulatorRoot = getAndroidSdkRoot() + "/emulator";
        new File(androidEmulatorRoot).mkdirs();
        return androidEmulatorRoot;
    }

    public String getPlatformToolsFolderInAndroidSdk() {
        return getAndroidSdkRoot() + "/platform-tools";
    }

    public String getToolsFolderInAndroidSdk() {
        return getAndroidSdkRoot() + "/tools";
    }

    public String getBuildToolsFolderInAndroidSdk() {
        return getAndroidSdkRoot() + "/build-tools";
    }

    public String getOutputForCompiledFiles(int index) {
        return tmpFolder + "/libs/libtest" + index;
    }

    public String getLibsFolderInAndroidTestedModuleTmpFolder() {
        return tmpFolder + "/tested-module/libs";
    }

    public String getLibsFolderInAndroidTmpFolder() {
        return tmpFolder + "/libs";
    }

    public String getSrcFolderInAndroidTmpFolder() {
        return tmpFolder + "/src";
    }

    public String getAndroidTmpFolder() {
        return tmpFolder;
    }

    public String getAndroidSdkRoot() {
        return getDependenciesRoot() + "/android-sdk";
    }

    public String getDependenciesRoot() {
        return rootFolder + "/android.tests.dependencies";
    }

    public String getGradleBinFolder() {
        return getDependenciesRoot() + "/gradle-" + SDKDownloader.GRADLE_VERSION + "/bin";
    }

    public String getRootForDownload() {
        return getDependenciesRoot() + "/download";
    }

    public String getAndroidModuleRoot() {
        return rootFolder + "/compiler/android-tests/android-module";
    }

    public String getTmpFolder() {
        return tmpFolder;
    }
}
