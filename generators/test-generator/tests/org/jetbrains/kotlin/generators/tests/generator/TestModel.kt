/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tests.generator

import org.jetbrains.kotlin.utils.Printer

interface TestEntityModel {
    val name: String
    val dataString: String?
}

interface TestClassModel : TestEntityModel {
    val innerTestClasses: Collection<TestClassModel>
    val methods: Collection<MethodModel>
    val isEmpty: Boolean
    val dataPathRoot: String?
}

interface MethodModel : TestEntityModel {
    fun shouldBeGenerated(): Boolean = true
    fun generateSignature(p: Printer)
    fun generateBody(p: Printer)
}

interface TestMethodModel : MethodModel {
    override fun generateSignature(p: Printer) {
        p.print("public void $name() throws Exception")
    }
}   
