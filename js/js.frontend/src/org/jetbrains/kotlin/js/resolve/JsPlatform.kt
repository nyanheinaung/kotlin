/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve

import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.resolve.MultiTargetPlatform
import org.jetbrains.kotlin.resolve.PlatformConfigurator
import org.jetbrains.kotlin.resolve.TargetPlatform
import org.jetbrains.kotlin.storage.StorageManager

object JsPlatform : TargetPlatform("JS") {
    override fun computePlatformSpecificDefaultImports(storageManager: StorageManager, result: MutableList<ImportPath>) {
        result.add(ImportPath.fromString("kotlin.js.*"))
    }

    override val platformConfigurator: PlatformConfigurator = JsPlatformConfigurator

    val builtIns: KotlinBuiltIns
        get() = DefaultBuiltIns.Instance

    override val multiTargetPlatform = MultiTargetPlatform.Specific(platformName)

    override val excludedImports: List<FqName> =
        listOf("Promise", "Date", "Console", "Math", "RegExp", "RegExpMatch", "Json", "json").map { FqName("kotlin.js.$it") }
}
