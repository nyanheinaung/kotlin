/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaField
import java.lang.reflect.Field

class ReflectJavaField(override val member: Field) : ReflectJavaMember(), JavaField {
    override val isEnumEntry: Boolean
        get() = member.isEnumConstant

    override val type: ReflectJavaType
        get() = ReflectJavaType.create(member.genericType)

    override val initializerValue: Any? get() = null
    override val hasConstantNotNullInitializer get() = false
}
