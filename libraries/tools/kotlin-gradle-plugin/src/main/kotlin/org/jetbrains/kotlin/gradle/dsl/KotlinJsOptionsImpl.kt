/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.dsl

import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments

internal class KotlinJsOptionsImpl : KotlinJsOptionsBase() {
    override var freeCompilerArgs: List<String> = listOf()

    override fun updateArguments(args: K2JSCompilerArguments) {
        super.updateArguments(args)
        copyFreeCompilerArgsToArgs(args)
    }
}
