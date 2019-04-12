/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.kotlin.test

import org.kotlin.annotationProcessor.TestAnnotation
import java.awt.Color

@TestAnnotation
class SimpleClass

class Test(val a: String) {
    companion object {
        val a = Color.PINK
    }
}