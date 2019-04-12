/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.AnnotationValue
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.EnumValue
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.types.Variance

fun KotlinBuiltIns.createDeprecatedAnnotation(
        message: String,
        replaceWith: String = "",
        level: String = "WARNING"
): AnnotationDescriptor {
    val replaceWithAnnotation = BuiltInAnnotationDescriptor(
            this,
            KotlinBuiltIns.FQ_NAMES.replaceWith,
            mapOf(
                    REPLACE_WITH_EXPRESSION_NAME to StringValue(replaceWith),
                    REPLACE_WITH_IMPORTS_NAME to ArrayValue(emptyList()) { module ->
                        module.builtIns.getArrayType(Variance.INVARIANT, stringType)
                    }
            )
    )

    return BuiltInAnnotationDescriptor(
            this,
            KotlinBuiltIns.FQ_NAMES.deprecated,
            mapOf(
                    DEPRECATED_MESSAGE_NAME to StringValue(message),
                    DEPRECATED_REPLACE_WITH_NAME to AnnotationValue(replaceWithAnnotation),
                    DEPRECATED_LEVEL_NAME to EnumValue(
                            ClassId.topLevel(KotlinBuiltIns.FQ_NAMES.deprecationLevel),
                            Name.identifier(level)
                    )
            )
    )
}

private val DEPRECATED_MESSAGE_NAME = Name.identifier("message")
private val DEPRECATED_REPLACE_WITH_NAME = Name.identifier("replaceWith")
private val DEPRECATED_LEVEL_NAME = Name.identifier("level")
private val REPLACE_WITH_EXPRESSION_NAME = Name.identifier("expression")
private val REPLACE_WITH_IMPORTS_NAME = Name.identifier("imports")
