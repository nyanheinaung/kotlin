/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.kotlin.test

import org.junit.Test
import org.junit.Assert.*
import test.generated.simpleClassName

class AnnotationTest {
    @Test fun testSimple() {
        assertEquals("SimpleClass", SimpleClass().simpleClassName)
    }
}