/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl

import com.intellij.psi.*
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

abstract class JavaAnnotationArgumentImpl(
        override val name: Name?
) : JavaAnnotationArgument {
    companion object Factory {
        fun create(argument: PsiAnnotationMemberValue, name: Name?): JavaAnnotationArgument {
            if (argument is PsiClassObjectAccessExpression) {
                return JavaClassObjectAnnotationArgumentImpl(argument, name)
            }

            val value = JavaPsiFacade.getInstance(argument.project).constantEvaluationHelper.computeConstantExpression(argument)
            if (value is Enum<*>) {
                return JavaEnumValueAnnotationArgumentImpl(argument as PsiReferenceExpression, name)
            }

            if (value != null || argument is PsiLiteralExpression) {
                return JavaLiteralAnnotationArgumentImpl(name, value)
            }

            return when (argument) {
                is PsiReferenceExpression -> JavaEnumValueAnnotationArgumentImpl(argument, name)
                is PsiArrayInitializerMemberValue -> JavaArrayAnnotationArgumentImpl(argument, name)
                is PsiAnnotation -> JavaAnnotationAsAnnotationArgumentImpl(argument, name)
                else -> throw UnsupportedOperationException("Unsupported annotation argument type: " + argument)
            }
        }
    }
}

class JavaLiteralAnnotationArgumentImpl(
        override val name: Name?,
        override val value: Any?
) : JavaLiteralAnnotationArgument

class JavaArrayAnnotationArgumentImpl(
        private val psiValue: PsiArrayInitializerMemberValue,
        name: Name?
) : JavaAnnotationArgumentImpl(name), JavaArrayAnnotationArgument {
    override fun getElements() = psiValue.initializers.map { JavaAnnotationArgumentImpl.create(it, null) }
}

class JavaEnumValueAnnotationArgumentImpl(
        private val psiReference: PsiReferenceExpression,
        name: Name?
) : JavaAnnotationArgumentImpl(name), JavaEnumValueAnnotationArgument {
    override val enumClassId: ClassId?
        get() {
            val element = psiReference.resolve()
            if (element is PsiEnumConstant) {
                return JavaFieldImpl(element).containingClass.classId
            }

            val fqName = (psiReference.qualifier as? PsiReferenceExpression)?.qualifiedName ?: return null
            // TODO: find a way to construct a correct name (with nested classes) for unresolved enums
            return ClassId.topLevel(FqName(fqName))
        }

    override val entryName: Name?
        get() = psiReference.referenceName?.let(Name::identifier)
}

class JavaClassObjectAnnotationArgumentImpl(
        private val psiExpression: PsiClassObjectAccessExpression,
        name: Name?
) : JavaAnnotationArgumentImpl(name), JavaClassObjectAnnotationArgument {
    override fun getReferencedType() = JavaTypeImpl.create(psiExpression.operand.type)
}

class JavaAnnotationAsAnnotationArgumentImpl(
        private val psiAnnotation: PsiAnnotation,
        name: Name?
) : JavaAnnotationArgumentImpl(name), JavaAnnotationAsAnnotationArgument {
    override fun getAnnotation() = JavaAnnotationImpl(psiAnnotation)
}
