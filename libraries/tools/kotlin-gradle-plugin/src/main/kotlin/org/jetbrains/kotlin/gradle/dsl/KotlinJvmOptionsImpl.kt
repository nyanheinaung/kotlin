/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.dsl

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments

internal class KotlinJvmOptionsImpl : KotlinJvmOptionsBase() {
    override var freeCompilerArgs: List<String> = listOf()

    override fun updateArguments(args: K2JVMCompilerArguments) {
        super.updateArguments(args)
        copyFreeCompilerArgsToArgs(args)
    }
}
