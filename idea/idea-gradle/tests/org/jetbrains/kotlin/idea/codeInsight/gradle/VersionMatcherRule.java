/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.idea.codeInsight.gradle;

import org.gradle.util.GradleVersion;
import org.hamcrest.CoreMatchers;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.tooling.annotation.TargetVersions;
import org.jetbrains.plugins.gradle.tooling.util.VersionMatcher;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

// copy of org.jetbrains.plugins.gradle.tooling.VersionMatcherRule
public class VersionMatcherRule extends TestWatcher {

    @Nullable
    private CustomMatcher myMatcher;

    @NotNull
    public Matcher getMatcher() {
        return myMatcher != null ? myMatcher : CoreMatchers.anything();
    }

    @Override
    protected void starting(Description d) {
        final TargetVersions targetVersions = d.getAnnotation(TargetVersions.class);
        if (targetVersions == null) return;

        myMatcher = new CustomMatcher<String>("Gradle version '" + targetVersions.value() + "'") {
            @Override
            public boolean matches(Object item) {
                return item instanceof String && new VersionMatcher(GradleVersion.version(item.toString())).isVersionMatch(targetVersions);
            }
        };
    }
}
