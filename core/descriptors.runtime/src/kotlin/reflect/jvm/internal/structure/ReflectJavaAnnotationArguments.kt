/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

abstract class ReflectJavaAnnotationArgument(
    override val name: Name?
) : JavaAnnotationArgument {
    companion object Factory {
        fun create(value: Any, name: Name?): ReflectJavaAnnotationArgument {
            return when {
                value::class.java.isEnumClassOrSpecializedEnumEntryClass() -> ReflectJavaEnumValueAnnotationArgument(name, value as Enum<*>)
                value is Annotation -> ReflectJavaAnnotationAsAnnotationArgument(name, value)
                value is Array<*> -> ReflectJavaArrayAnnotationArgument(name, value)
                value is Class<*> -> ReflectJavaClassObjectAnnotationArgument(name, value)
                else -> ReflectJavaLiteralAnnotationArgument(name, value)
            }
        }
    }
}

class ReflectJavaLiteralAnnotationArgument(
    name: Name?,
    override val value: Any
) : ReflectJavaAnnotationArgument(name), JavaLiteralAnnotationArgument

class ReflectJavaArrayAnnotationArgument(
    name: Name?,
    private val values: Array<*>
) : ReflectJavaAnnotationArgument(name), JavaArrayAnnotationArgument {
    override fun getElements() = values.map { create(it!!, null) }
}

class ReflectJavaEnumValueAnnotationArgument(
    name: Name?,
    private val value: Enum<*>
) : ReflectJavaAnnotationArgument(name), JavaEnumValueAnnotationArgument {
    override val enumClassId: ClassId?
        get() {
            val clazz = value::class.java
            val enumClass = if (clazz.isEnum) clazz else clazz.enclosingClass
            return enumClass.classId
        }

    override val entryName: Name?
        get() = Name.identifier(value.name)
}

class ReflectJavaClassObjectAnnotationArgument(
    name: Name?,
    private val klass: Class<*>
) : ReflectJavaAnnotationArgument(name), JavaClassObjectAnnotationArgument {
    override fun getReferencedType(): JavaType = ReflectJavaType.create(klass)
}

class ReflectJavaAnnotationAsAnnotationArgument(
    name: Name?,
    private val annotation: Annotation
) : ReflectJavaAnnotationArgument(name), JavaAnnotationAsAnnotationArgument {
    override fun getAnnotation(): JavaAnnotation = ReflectJavaAnnotation(annotation)
}
