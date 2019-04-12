/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaValueParameter
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ReflectJavaValueParameter(
    override val type: ReflectJavaType,
    private val reflectAnnotations: Array<Annotation>,
    private val reflectName: String?,
    override val isVararg: Boolean
) : ReflectJavaElement(), JavaValueParameter {
    override val annotations: List<ReflectJavaAnnotation>
        get() = reflectAnnotations.getAnnotations()

    override fun findAnnotation(fqName: FqName) =
        reflectAnnotations.findAnnotation(fqName)

    override val isDeprecatedInJavaDoc: Boolean
        get() = false

    override val name: Name?
        get() = reflectName?.let(Name::guessByFirstCharacter)

    override fun toString() = this::class.java.name + ": " + (if (isVararg) "vararg " else "") + name + ": " + type
}
