/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.arguments

class K2MetadataCompilerArguments : CommonCompilerArguments() {
    companion object {
        @JvmStatic private val serialVersionUID = 0L
    }

    @Argument(value = "-d", valueDescription = "<directory|jar>", description = "Destination for generated .kotlin_metadata files")
    var destination: String? by NullableStringFreezableVar(null)

    @Argument(
            value = "-classpath",
            shortName = "-cp",
            valueDescription = "<path>",
            description = "Paths where to find library .kotlin_metadata files"
    )
    var classpath: String? by NullableStringFreezableVar(null)

    @Argument(value = "-module-name", valueDescription = "<name>", description = "Name of the generated .kotlin_module file")
    var moduleName: String? by NullableStringFreezableVar(null)

    @Argument(
        value = "-Xjps",
        description = "Enable in JPS"
    )
    var enabledInJps: Boolean by FreezableVar(false)
}
