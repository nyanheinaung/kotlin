/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.testing

import org.jetbrains.kotlin.gradle.targets.js.tasks.KotlinNodeJsTestRunnerCliArgs

@Suppress("EnumEntryName")
enum class IgnoredTestSuites(val cli: KotlinNodeJsTestRunnerCliArgs.IgnoredTestSuitesReporting) {
    hide(KotlinNodeJsTestRunnerCliArgs.IgnoredTestSuitesReporting.skip),
    showWithContents(KotlinNodeJsTestRunnerCliArgs.IgnoredTestSuitesReporting.reportAllInnerTestsAsIgnored),
    showWithoutContents(KotlinNodeJsTestRunnerCliArgs.IgnoredTestSuitesReporting.reportAsIgnoredTest)
}

@Suppress("EnumEntryName")
enum class TestsGrouping {
    none,
    root,
    leaf;
}

