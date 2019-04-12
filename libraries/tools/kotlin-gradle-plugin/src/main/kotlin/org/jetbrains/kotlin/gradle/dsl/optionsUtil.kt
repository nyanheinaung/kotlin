/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.dsl

import org.jetbrains.kotlin.cli.common.arguments.CommonToolArguments

fun KotlinCommonToolOptions.copyFreeCompilerArgsToArgs(args: CommonToolArguments) {
    // cast to List<Any> is important because in Groovy a GString can be inside of a list
    val freeArgs = (freeCompilerArgs as List<Any>).map(Any::toString)
    args.freeArgs += freeArgs
}