/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlinPackage

import javaPackage.*
import javaPackage2.*

public class KotlinClass() {
    fun foo() {}
}

fun main() {
    JavaClass().foo()
    JavaClass2().foo()
}

