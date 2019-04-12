/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaElement
import javax.lang.model.element.Element

open class SymbolBasedElement<out T : Element>(
        val element: T,
        val javac: JavacWrapper
) : JavaElement {

    override fun equals(other: Any?) = (other as? SymbolBasedElement<*>)?.element == element

    override fun hashCode() = element.hashCode()

    override fun toString() = element.simpleName.toString()

}