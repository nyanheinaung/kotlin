/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.metadata.jvm.deserialization.PackageParts
import org.jetbrains.kotlin.name.FqName

class PackagePartRegistry {
    val parts = mutableMapOf<FqName, PackageParts>()

    fun addPart(packageFqName: FqName, partInternalName: String, facadeInternalName: String?) {
        parts.computeIfAbsent(packageFqName) { PackageParts(it.asString()) }.addPart(partInternalName, facadeInternalName)
    }
}
