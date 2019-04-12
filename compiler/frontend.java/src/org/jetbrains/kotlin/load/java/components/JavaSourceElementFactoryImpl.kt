/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.load.java.sources.JavaSourceElement
import org.jetbrains.kotlin.load.java.sources.JavaSourceElementFactory
import org.jetbrains.kotlin.load.java.structure.JavaElement
import org.jetbrains.kotlin.load.java.structure.impl.JavaElementImpl
import org.jetbrains.kotlin.resolve.source.PsiSourceElement

private class JavaSourceElementImpl(override val javaElement: JavaElement) : PsiSourceElement, JavaSourceElement {
    override val psi: PsiElement?
        get() = (javaElement as? JavaElementImpl<*>)?.psi
}

class JavaSourceElementFactoryImpl : JavaSourceElementFactory {
    override fun source(javaElement: JavaElement): JavaSourceElement = JavaSourceElementImpl(javaElement)
}
