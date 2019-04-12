/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.build

import org.jetbrains.jps.builders.JpsBuildTestCase
import org.jetbrains.jps.model.library.JpsLibrary
import org.jetbrains.kotlin.compilerRunner.JpsKotlinCompilerRunner

abstract class BaseKotlinJpsBuildTestCase : JpsBuildTestCase() {
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        System.setProperty("kotlin.jps.tests", "true")
    }

    @Throws(Exception::class)
    override fun tearDown() {
        System.clearProperty("kotlin.jps.tests")
        super.tearDown()
        myModel = null
        myBuildParams.clear()
        JpsKotlinCompilerRunner.releaseCompileServiceSession()
    }

    private val libraries = mutableMapOf<String, JpsLibrary>()

    protected fun requireLibrary(library: KotlinJpsLibrary) = libraries.getOrPut(library.id) {
        library.create(myProject)
    }
}