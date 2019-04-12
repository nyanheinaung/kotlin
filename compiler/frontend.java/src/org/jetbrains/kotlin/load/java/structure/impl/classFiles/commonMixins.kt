/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl.classFiles

import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.load.java.JavaVisibilities
import org.jetbrains.kotlin.load.java.structure.JavaModifierListOwner
import org.jetbrains.kotlin.load.java.structure.MapBasedJavaAnnotationOwner
import org.jetbrains.org.objectweb.asm.Opcodes

internal const val ASM_API_VERSION_FOR_CLASS_READING = Opcodes.API_VERSION

internal interface BinaryJavaModifierListOwner : JavaModifierListOwner, MapBasedJavaAnnotationOwner {
    val access: Int

    fun isSet(flag: Int) = access.isSet(flag)

    override val isAbstract get() = isSet(Opcodes.ACC_ABSTRACT)
    override val isStatic get() = isSet(Opcodes.ACC_STATIC)
    override val isFinal get() = isSet(Opcodes.ACC_FINAL)
    override val visibility: Visibility
        get() = when {
            isSet(Opcodes.ACC_PRIVATE) -> Visibilities.PRIVATE
            isSet(Opcodes.ACC_PROTECTED) ->
                if (isStatic) JavaVisibilities.PROTECTED_STATIC_VISIBILITY else JavaVisibilities.PROTECTED_AND_PACKAGE
            isSet(Opcodes.ACC_PUBLIC) -> Visibilities.PUBLIC
            else -> JavaVisibilities.PACKAGE_VISIBILITY
        }

    override val isDeprecatedInJavaDoc get() = isSet(Opcodes.ACC_DEPRECATED)
}

internal fun Int.isSet(flag: Int) = this and flag != 0
