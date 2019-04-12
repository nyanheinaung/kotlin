/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.load.java.JvmAbi
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import java.util.*

interface InnerClassConsumer {
    fun addInnerClassInfoFromAnnotation(classDescriptor: ClassDescriptor)

    companion object {

        fun classForInnerClassRecord(descriptor: ClassDescriptor, defaultImpls: Boolean): ClassDescriptor? {
            if (defaultImpls) {
                if (DescriptorUtils.isLocal(descriptor)) return null
                val classDescriptorImpl = ClassDescriptorImpl(
                    descriptor, Name.identifier(JvmAbi.DEFAULT_IMPLS_CLASS_NAME),
                    Modality.FINAL, ClassKind.CLASS, Collections.emptyList(), SourceElement.NO_SOURCE,
                    /* isExternal = */ false, LockBasedStorageManager.NO_LOCKS
                )

                classDescriptorImpl.initialize(MemberScope.Empty, emptySet(), null)
                return classDescriptorImpl
            } else {
                return if (DescriptorUtils.isTopLevelDeclaration(descriptor)) null else descriptor
            }
        }
    }
}