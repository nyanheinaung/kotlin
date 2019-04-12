/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorNonRoot
import org.jetbrains.kotlin.fileClasses.JvmFileClassUtil
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.OverloadFilter
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedCallableMemberDescriptor
import java.util.*

object JvmOverloadFilter : OverloadFilter {
    override fun filterPackageMemberOverloads(overloads: Collection<DeclarationDescriptorNonRoot>): Collection<DeclarationDescriptorNonRoot> {
        val result = ArrayList<DeclarationDescriptorNonRoot>()

        val sourceClassesFQNs = HashSet<FqName>()
        for (overload in overloads) {
            val file = DescriptorToSourceUtils.getContainingFile(overload) ?: continue
            result.add(overload)
            sourceClassesFQNs.add(JvmFileClassUtil.getFileClassInfoNoResolve(file).fileClassFqName)
        }

        for (overload in overloads) {
            if (overload is ConstructorDescriptor) continue
            if (overload !is DeserializedCallableMemberDescriptor) continue

            val implClassFQN = JvmFileClassUtil.getPartFqNameForDeserialized(overload)
            if (implClassFQN !in sourceClassesFQNs) {
                result.add(overload)
            }
        }

        return result
    }
}
