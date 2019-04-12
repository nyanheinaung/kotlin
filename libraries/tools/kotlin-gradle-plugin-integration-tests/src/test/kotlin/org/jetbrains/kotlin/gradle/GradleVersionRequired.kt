/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle

import org.gradle.util.GradleVersion
import org.junit.Assume

sealed class GradleVersionRequired(val minVersion: String, val maxVersion: String?) {
    companion object {
        const val OLDEST_SUPPORTED = "4.1"
    }

    class Exact(version: String) : GradleVersionRequired(version, version)

    class AtLeast(version: String) : GradleVersionRequired(version, null)

    class InRange(minVersion: String, maxVersion: String) : GradleVersionRequired(minVersion, maxVersion)

    class Until(maxVersion: String) : GradleVersionRequired(OLDEST_SUPPORTED, maxVersion)

    object None : GradleVersionRequired(GradleVersionRequired.OLDEST_SUPPORTED, null)
}


fun BaseGradleIT.Project.chooseWrapperVersionOrFinishTest(): String {
    val gradleVersionForTests = System.getProperty("kotlin.gradle.version.for.tests")?.toGradleVersion()
    val minVersion = gradleVersionRequirement.minVersion.toGradleVersion()
    val maxVersion = gradleVersionRequirement.maxVersion?.toGradleVersion()

    if (gradleVersionForTests == null) {
        return minVersion.version
    }

    Assume.assumeTrue(minVersion <= gradleVersionForTests && (maxVersion == null || gradleVersionForTests <= maxVersion))

    return gradleVersionForTests.version
}

private fun String.toGradleVersion() = GradleVersion.version(this)