/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.arguments

class K2JSDceArguments : CommonToolArguments() {
    companion object {
        @JvmStatic private val serialVersionUID = 0L
    }

    @Argument(
            value = "-output-dir",
            valueDescription = "<path>",
            description = "Output directory"
    )
    @GradleOption(DefaultValues.StringNullDefault::class)
    var outputDirectory: String? by NullableStringFreezableVar(null)

    @Argument(
            value = "-keep",
            valueDescription = "<fully.qualified.name[,]>",
            description = "List of fully-qualified names of declarations that shouldn't be eliminated"
    )
    var declarationsToKeep: Array<String>? by FreezableVar(null)

    @Argument(
            value = "-Xprint-reachability-info",
            description = "Print declarations marked as reachable"
    )
    var printReachabilityInfo: Boolean by FreezableVar(false)

    @Argument(
            value = "-dev-mode",
            description = "Development mode: don't strip out any code, just copy dependencies"
    )
    @GradleOption(DefaultValues.BooleanFalseDefault::class)
    var devMode: Boolean by FreezableVar(false)
}
