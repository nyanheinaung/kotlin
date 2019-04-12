/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.JvmCodegenUtil.isJvmInterface
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.resolve.jvm.AsmTypes.OBJECT_TYPE
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.org.objectweb.asm.Type

class SuperClassInfo(
    val type: Type,
    // null means java/lang/Object or irrelevant
    val kotlinType: KotlinType?
) {

    companion object {
        @JvmStatic
        fun getSuperClassInfo(descriptor: ClassDescriptor, typeMapper: KotlinTypeMapper): SuperClassInfo {
            if (descriptor.kind == ClassKind.INTERFACE) {
                return SuperClassInfo(OBJECT_TYPE, null)
            }

            for (supertype in descriptor.typeConstructor.supertypes) {
                val superClass = supertype.constructor.declarationDescriptor
                if (superClass != null && !isJvmInterface(superClass)) {
                    return SuperClassInfo(typeMapper.mapClass(superClass), supertype)
                }
            }

            return SuperClassInfo(OBJECT_TYPE, null)
        }
    }

}