/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.arguments

import java.io.Serializable

abstract class CommonToolArguments : Freezable(), Serializable {
    companion object {
        @JvmStatic
        private val serialVersionUID = 0L
    }

    var freeArgs: List<String> by FreezableVar(emptyList())

    @Transient
    var errors: ArgumentParseErrors? = null

    @Argument(value = "-help", shortName = "-h", description = "Print a synopsis of standard options")
    var help: Boolean by FreezableVar(false)

    @Argument(value = "-X", description = "Print a synopsis of advanced options")
    var extraHelp: Boolean by FreezableVar(false)

    @Argument(value = "-version", description = "Display compiler version")
    var version: Boolean by FreezableVar(false)

    @GradleOption(DefaultValues.BooleanFalseDefault::class)
    @Argument(value = "-verbose", description = "Enable verbose logging output")
    var verbose: Boolean by FreezableVar(false)

    @GradleOption(DefaultValues.BooleanFalseDefault::class)
    @Argument(value = "-nowarn", description = "Generate no warnings")
    var suppressWarnings: Boolean by FreezableVar(false)

    @GradleOption(DefaultValues.BooleanFalseDefault::class)
    @Argument(value = "-Werror", description = "Report an error if there are any warnings")
    var allWarningsAsErrors: Boolean by FreezableVar(false)

    var internalArguments: List<InternalArgument> by FreezableVar(emptyList())
}
