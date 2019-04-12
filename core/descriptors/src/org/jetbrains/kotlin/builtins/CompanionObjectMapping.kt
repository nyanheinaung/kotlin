/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.builtins

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import java.util.*

object CompanionObjectMapping {
    private val classIds =
        (PrimitiveType.NUMBER_TYPES.map(KotlinBuiltIns::getPrimitiveFqName) +
                KotlinBuiltIns.FQ_NAMES.string.toSafe() +
                KotlinBuiltIns.FQ_NAMES._boolean.toSafe() +
                KotlinBuiltIns.FQ_NAMES._enum.toSafe()).mapTo(linkedSetOf<ClassId>(), ClassId::topLevel)

    fun allClassesWithIntrinsicCompanions(): Set<ClassId> =
        Collections.unmodifiableSet(classIds)

    fun isMappedIntrinsicCompanionObject(classDescriptor: ClassDescriptor): Boolean =
        DescriptorUtils.isCompanionObject(classDescriptor) && classDescriptor.classId?.outerClassId in classIds
}
