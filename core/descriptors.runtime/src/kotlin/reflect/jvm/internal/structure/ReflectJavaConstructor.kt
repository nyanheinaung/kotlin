/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaConstructor
import org.jetbrains.kotlin.load.java.structure.JavaValueParameter
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier

class ReflectJavaConstructor(override val member: Constructor<*>) : ReflectJavaMember(), JavaConstructor {
    // TODO: test local/anonymous classes
    override val valueParameters: List<JavaValueParameter>
        get() {
            val types = member.genericParameterTypes
            if (types.isEmpty()) return emptyList()

            val klass = member.declaringClass

            val realTypes = when {
                klass.declaringClass != null && !Modifier.isStatic(klass.modifiers) -> types.copyOfRange(1, types.size)
                else -> types
            }

            val annotations = member.parameterAnnotations
            val realAnnotations = when {
                annotations.size < realTypes.size -> throw IllegalStateException("Illegal generic signature: $member")
                annotations.size > realTypes.size -> annotations.copyOfRange(annotations.size - realTypes.size, annotations.size)
                else -> annotations
            }

            return getValueParameters(realTypes, realAnnotations, member.isVarArgs)
        }

    override val typeParameters: List<ReflectJavaTypeParameter>
        get() = member.typeParameters.map(::ReflectJavaTypeParameter)
}
