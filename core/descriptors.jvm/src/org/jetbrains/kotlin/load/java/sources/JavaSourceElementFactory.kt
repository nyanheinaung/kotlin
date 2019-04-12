/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.sources

import org.jetbrains.kotlin.load.java.structure.JavaElement
import org.jetbrains.kotlin.descriptors.SourceElement

interface JavaSourceElementFactory {
    fun source(javaElement: JavaElement): JavaSourceElement
}

interface JavaSourceElement : SourceElement {
    val javaElement: JavaElement
}
