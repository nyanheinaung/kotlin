/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg.gradle

open class NoArgExtension {
    internal val myAnnotations = mutableListOf<String>()
    internal val myPresets = mutableListOf<String>()

    open var invokeInitializers: Boolean = false

    open fun annotation(fqName: String) {
        myAnnotations.add(fqName)
    }

    open fun annotations(fqNames: List<String>) {
        myAnnotations.addAll(fqNames)
    }

    open fun annotations(vararg fqNames: String) {
        myAnnotations.addAll(fqNames)
    }
}