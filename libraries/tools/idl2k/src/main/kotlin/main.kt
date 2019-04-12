/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.idl2k

import org.jetbrains.idl2k.util.readCopyrightNoticeFromProfile
import java.io.*

fun main(args: Array<String>) {
    val webIdl = BuildWebIdl(
            mdnCacheFile = File("target/mdn-cache.txt"),
            srcDir = File("../../stdlib/js/idl"))

    println("Generating...")

    val copyrightNotice = readCopyrightNoticeFromProfile(File("../../../.idea/copyright/apache.xml"))
    webIdl.jsGenerator(File("../../stdlib/js/src/org.w3c"), copyrightNotice)
}
