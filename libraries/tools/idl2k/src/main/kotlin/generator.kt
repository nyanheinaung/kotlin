/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.idl2k

import java.io.File

fun BuildWebIdl.jsGenerator(outDir: File, copyrightNotice: String) {

    outDir.deleteRecursively()
    outDir.mkdirs()

    allPackages.forEach { pkg ->
        File(outDir, pkg + ".kt").bufferedWriter().use { w ->
            println("Generating for package $pkg...")
            w.appendln(copyrightNotice)
            w.appendln("// NOTE: THIS FILE IS AUTO-GENERATED, DO NOT EDIT!")
            w.appendln("// See libraries/tools/idl2k for details")

            w.appendln()
            w.appendln("@file:Suppress(\"NESTED_CLASS_IN_EXTERNAL_INTERFACE\")")
            w.appendln("package $pkg")
            w.appendln()

            w.appendln("import kotlin.js.*")
            allPackages.filter { it != pkg }.forEach { import ->
                w.appendln("import $import.*")
            }
            w.appendln()

            w.render(pkg, definitions, unions, repository.enums.values.toList(), mdnCache)
        }
    }
}