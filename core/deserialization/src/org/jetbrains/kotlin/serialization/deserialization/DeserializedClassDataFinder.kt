/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.name.ClassId

class DeserializedClassDataFinder(private val packageFragmentProvider: PackageFragmentProvider) : ClassDataFinder {
    override fun findClassData(classId: ClassId): ClassData? {
        val packageFragments = packageFragmentProvider.getPackageFragments(classId.packageFqName)
        for (fragment in packageFragments) {
            if (fragment !is DeserializedPackageFragment) continue

            fragment.classDataFinder.findClassData(classId)?.let { return it }
        }
        return null
    }
}
