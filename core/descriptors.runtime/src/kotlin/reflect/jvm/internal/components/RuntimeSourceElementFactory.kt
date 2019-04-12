/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.components

import org.jetbrains.kotlin.descriptors.SourceFile
import org.jetbrains.kotlin.load.java.sources.JavaSourceElement
import org.jetbrains.kotlin.load.java.sources.JavaSourceElementFactory
import org.jetbrains.kotlin.load.java.structure.JavaElement
import kotlin.reflect.jvm.internal.structure.ReflectJavaElement

object RuntimeSourceElementFactory : JavaSourceElementFactory {
    class RuntimeSourceElement(override val javaElement: ReflectJavaElement) : JavaSourceElement {
        override fun toString() = this::class.java.name + ": " + javaElement.toString()
        override fun getContainingFile(): SourceFile = SourceFile.NO_SOURCE_FILE
    }

    override fun source(javaElement: JavaElement): JavaSourceElement = RuntimeSourceElement(javaElement as ReflectJavaElement)
}
