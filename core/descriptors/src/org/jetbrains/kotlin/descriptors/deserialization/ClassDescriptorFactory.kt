/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.deserialization

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

interface ClassDescriptorFactory {
    fun shouldCreateClass(packageFqName: FqName, name: Name): Boolean

    fun createClass(classId: ClassId): ClassDescriptor?

    // Note: do not rely on this function to return _all_ classes. Some factories can not enumerate all classes that they're able to create
    fun getAllContributedClassesIfPossible(packageFqName: FqName): Collection<ClassDescriptor>
}
