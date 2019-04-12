/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl.classFiles

import com.intellij.util.containers.ContainerUtil
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

// They are only used for java class files, but potentially may be used in other cases
// It would be better to call them like JavaSomeTypeImpl, but these names are already occupied by the PSI based types
internal class PlainJavaArrayType(override val componentType: JavaType) : JavaArrayType
internal class PlainJavaWildcardType(override val bound: JavaType?, override val isExtends: Boolean) : JavaWildcardType
internal class PlainJavaPrimitiveType(override val type: PrimitiveType?) : JavaPrimitiveType

internal class PlainJavaClassifierType(
        // calculation of classifier and canonicalText
        classifierComputation: () -> ClassifierResolutionContext.Result,
        override val typeArguments: List<JavaType>
) : JavaClassifierType {
    private val classifierResolverResult by lazy(LazyThreadSafetyMode.NONE, classifierComputation)

    override val classifier get() = classifierResolverResult.classifier
    override val isRaw
        get() = typeArguments.isEmpty() &&
                classifierResolverResult.classifier?.safeAs<JavaClass>()?.typeParameters?.isNotEmpty() == true

    private var _annotations = emptyList<JavaAnnotation>()
    override val annotations get() = _annotations

    override fun findAnnotation(fqName: FqName) = annotations.find { it.classId?.asSingleFqName() == fqName }

    internal fun addAnnotation(annotation: JavaAnnotation) {
        if (_annotations.isEmpty()) {
            _annotations = ContainerUtil.newSmartList()
        }

        (_annotations as MutableList).add(annotation)
    }

    override val isDeprecatedInJavaDoc get() = false

    override val classifierQualifiedName: String
        get() = classifierResolverResult.qualifiedName

    // TODO: render arguments for presentable text
    override val presentableText: String
        get() = classifierQualifiedName
}
