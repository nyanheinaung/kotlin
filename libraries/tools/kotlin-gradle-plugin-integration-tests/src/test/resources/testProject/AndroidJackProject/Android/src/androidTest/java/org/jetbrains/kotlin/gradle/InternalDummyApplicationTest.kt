/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle

import android.app.Application
import android.test.ApplicationTestCase

class InternalDummyApplicationTest : ApplicationTestCase<Application>(Application::class.java) {
    init {
        val dummy = InternalDummy("World")
        assert("Hello World!" == dummy.greeting) { "Expected: 'Hello World!'. Actual value: ${dummy.greeting}" }

        // Check that the Java sources from the tested variant are available
        val bar = foo.FooJavaClass()
    }
}