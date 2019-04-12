/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests.gradle;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.kotlin.android.tests.OutputUtils;
import org.jetbrains.kotlin.android.tests.PathManager;
import org.jetbrains.kotlin.android.tests.run.RunResult;
import org.jetbrains.kotlin.android.tests.run.RunUtils;

import java.util.ArrayList;
import java.util.List;

public class GradleRunner {
    private final List<String> listOfCommands;

    public GradleRunner(PathManager pathManager) {
        listOfCommands = new ArrayList<>();
        String cmdName = SystemInfo.isWindows ? "gradle.bat" : "gradle";
        listOfCommands.add(pathManager.getGradleBinFolder() + "/" + cmdName);
        listOfCommands.add("--no-daemon");
        listOfCommands.add("--build-file");
        listOfCommands.add(pathManager.getTmpFolder() + "/build.gradle");
    }


    public void clean() {
        System.out.println("Building gradle project...");
        RunResult result = RunUtils.execute(generateCommandLine("clean"));
        OutputUtils.checkResult(result);
    }

    public void build() {
        System.out.println("Building gradle project...");
        GeneralCommandLine build = generateCommandLine("build");
        build.addParameter("--stacktrace");
        build.addParameter("--warn");
        RunResult result = RunUtils.execute(build);
        OutputUtils.checkResult(result);
    }

    public void installDebugAndroidTest() {
        System.out.println("Install tests...");
        OutputUtils.checkResult(RunUtils.execute(generateCommandLine("installDebug")));
        OutputUtils.checkResult(RunUtils.execute(generateCommandLine("installDebugAndroidTest")));
    }

    public void uninstallDebugAndroidTest() {
        System.out.println("Uninstall tests...");
        RunUtils.execute(generateCommandLine("uninstallDebugAndroidTest"));
        RunUtils.execute(generateCommandLine("uninstallDebug"));
    }

    public String connectedDebugAndroidTest() {
        System.out.println("Starting tests...");
        GeneralCommandLine test = generateCommandLine("connectedAndroidTest");
        test.addParameters("--stacktrace");
        //To avoid problem with discovering tests on Android in multidex build
        test.addParameters("-Pandroid.testInstrumentationRunnerArguments.class=org.jetbrains.kotlin.android.tests.CodegenTestCaseOnAndroid");
        return RunUtils.execute(test).getOutput();
    }

    private GeneralCommandLine generateCommandLine(String taskName) {
        GeneralCommandLine commandLine = new GeneralCommandLine(listOfCommands);
        commandLine.addParameter(taskName);
        return commandLine;
    }
}
