/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.builtins

import java.io.File

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "true")

    if (args.size < 2) {
        println(
"""Kotlin built-ins serializer

Usage: ... <destination dir> (<source dir>)+

Analyzes Kotlin sources found in the given source directories and serializes
found top-level declarations to <destination dir> (*.kotlin_builtins files)"""
        )
        return
    }

    val destDir = File(args[0])

    val srcDirs = args.drop(1).map(::File)
    assert(srcDirs.isNotEmpty()) { "At least one source directory should be specified" }

    val missing = srcDirs.filterNot(File::exists)
    assert(missing.isEmpty()) { "These source directories are missing: $missing" }

    BuiltInsSerializer(dependOnOldBuiltIns = false).serialize(destDir, srcDirs, listOf()) { totalSize, totalFiles ->
        println("Total bytes written: $totalSize to $totalFiles files")
    }
}
