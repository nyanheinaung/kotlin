/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.builtins

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.test.ConfigurationKind
import org.jetbrains.kotlin.test.TestJdkKind

class BuiltInsWithUnexpectedJDKMembersTest : AbstractBuiltInsWithJDKMembersTest() {
    override fun createEnvironment(): KotlinCoreEnvironment {
        return createEnvironmentWithJdk(ConfigurationKind.JDK_ONLY, TestJdkKind.MODIFIED_MOCK_JDK)
    }

    fun testLoadBuiltIns() {
        doTest("newMethods")
    }
}
