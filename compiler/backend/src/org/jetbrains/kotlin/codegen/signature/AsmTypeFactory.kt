/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.signature

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.load.kotlin.JvmTypeFactory
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.org.objectweb.asm.Type

object AsmTypeFactory : JvmTypeFactory<Type> {
    override fun boxType(possiblyPrimitiveType: Type) = AsmUtil.boxType(possiblyPrimitiveType)
    override fun createFromString(representation: String) = Type.getType(representation)
    override fun createObjectType(internalName: String) = Type.getObjectType(internalName)
    override fun toString(type: Type) = type.descriptor

    override val javaLangClassType: Type
        get() = AsmTypes.JAVA_CLASS_TYPE
}
