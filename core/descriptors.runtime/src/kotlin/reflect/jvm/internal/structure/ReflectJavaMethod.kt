/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument
import org.jetbrains.kotlin.load.java.structure.JavaMethod
import org.jetbrains.kotlin.load.java.structure.JavaValueParameter
import java.lang.reflect.Method

class ReflectJavaMethod(override val member: Method) : ReflectJavaMember(), JavaMethod {
    override val valueParameters: List<JavaValueParameter>
        get() = getValueParameters(member.genericParameterTypes, member.parameterAnnotations, member.isVarArgs)

    override val returnType: ReflectJavaType
        get() = ReflectJavaType.create(member.genericReturnType)

    override val annotationParameterDefaultValue: JavaAnnotationArgument?
        get() = member.defaultValue?.let { ReflectJavaAnnotationArgument.create(it, null) }

    override val typeParameters: List<ReflectJavaTypeParameter>
        get() = member.typeParameters.map(::ReflectJavaTypeParameter)
}
