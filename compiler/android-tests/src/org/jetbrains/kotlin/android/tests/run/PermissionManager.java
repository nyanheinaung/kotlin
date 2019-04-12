/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests.run;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.kotlin.android.tests.PathManager;
import org.jetbrains.kotlin.android.tests.download.SDKDownloader;

import java.io.File;

public class PermissionManager {
    private PermissionManager() {
    }

    public static void setPermissions(PathManager pathManager) {
        if (!SystemInfo.isWindows) {
            setExecPermissionForSimpleNamedFiles(new File(pathManager.getToolsFolderInAndroidSdk()));
            setExecPermissionForSimpleNamedFiles(new File(pathManager.getToolsFolderInAndroidSdk() + "/bin64"));
            setExecPermissionForSimpleNamedFiles(new File(pathManager.getBuildToolsFolderInAndroidSdk() + "/" + SDKDownloader.BUILD_TOOLS));
            setExecPermissionForSimpleNamedFiles(new File(pathManager.getPlatformToolsFolderInAndroidSdk()));
            setExecPermissionForSimpleNamedFiles(new File(pathManager.getToolsFolderInAndroidSdk() +"/qemu/linux-x86_64"));
            RunUtils.execute(generateChmodCmd(pathManager.getGradleBinFolder() + "/gradle"));
        }
    }

    private static void setExecPermissionForSimpleNamedFiles(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.getName().contains(".")) {
                    RunUtils.execute(generateChmodCmd(file.getAbsolutePath()));
                }
            }
        }
    }

    private static GeneralCommandLine generateChmodCmd(String path) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath("chmod");
        commandLine.addParameter("a+x");
        commandLine.addParameter(path);
        return commandLine;
    }
}
