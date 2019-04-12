/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.testFramework

import java.lang.instrument.Instrumentation

@Suppress("unused")
object TestInstrumentationAgent {
    @JvmStatic
    fun premain(arg: String?, instrumentation: Instrumentation) {

        val arguments = arg.orEmpty().split(",")

        val debug = "debug" in arguments
        if (debug) {
            println("org.jetbrains.kotlin.testFramework.TestInstrumentationAgent: premain")
        }
        instrumentation.addTransformer(MockApplicationCreationTracingInstrumenter(debug))
    }
}