/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.jvm.JavaToKotlinClassMap
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.deprecation.Deprecation
import org.jetbrains.kotlin.resolve.deprecation.DeprecationLevelValue
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable
import org.jetbrains.kotlin.utils.extractRadix

sealed class JavaDefaultValue
class EnumEntry(val descriptor: ClassDescriptor) : JavaDefaultValue()
class Constant(val value: Any) : JavaDefaultValue()

fun KotlinType.lexicalCastFrom(value: String): JavaDefaultValue? {
    val typeDescriptor = constructor.declarationDescriptor
    if (typeDescriptor is ClassDescriptor && typeDescriptor.kind == ClassKind.ENUM_CLASS) {
        val descriptor = typeDescriptor.unsubstitutedInnerClassesScope.getContributedClassifier(
            Name.identifier(value),
            NoLookupLocation.FROM_BACKEND
        )

        return if (descriptor is ClassDescriptor && descriptor.kind == ClassKind.ENUM_ENTRY) EnumEntry(descriptor) else null
    }

    val type = this.makeNotNullable()
    val (number, radix) = extractRadix(value)
    val result: Any? = try {
        when {
            KotlinBuiltIns.isBoolean(type) -> value.toBoolean()
            KotlinBuiltIns.isChar(type) -> value.singleOrNull()
            KotlinBuiltIns.isByte(type) -> number.toByteOrNull(radix)
            KotlinBuiltIns.isShort(type) -> number.toShortOrNull(radix)
            KotlinBuiltIns.isInt(type) -> number.toIntOrNull(radix)
            KotlinBuiltIns.isLong(type) -> number.toLongOrNull(radix)
            KotlinBuiltIns.isFloat(type) -> value.toFloatOrNull()
            KotlinBuiltIns.isDouble(type) -> value.toDoubleOrNull()
            KotlinBuiltIns.isString(type) -> value
            else -> null
        }
    } catch (e: IllegalArgumentException) {
        null
    }

    return if (result != null) Constant(result) else null
}

class DeprecationCausedByFunctionN(override val target: DeclarationDescriptor) : Deprecation {
    override val deprecationLevel: DeprecationLevelValue
        get() = DeprecationLevelValue.ERROR
    override val message: String?
        get() = "Java members containing references to ${JavaToKotlinClassMap.FUNCTION_N_FQ_NAME} are not supported"
}
