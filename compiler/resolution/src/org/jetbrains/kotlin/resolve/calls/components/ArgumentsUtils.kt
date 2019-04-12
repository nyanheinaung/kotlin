/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.components

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.model.CollectionLiteralKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.SimpleKotlinCallArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.isParameterOfAnnotation
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.multiplatform.ExpectedActualResolver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.intersectWrappedTypes
import org.jetbrains.kotlin.utils.DFS
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

internal fun unexpectedArgument(argument: KotlinCallArgument): Nothing =
    error("Unexpected argument type: $argument, ${argument.javaClass.canonicalName}.")

// if expression is not stable and has smart casts, then we create this type
internal val ReceiverValueWithSmartCastInfo.unstableType: UnwrappedType?
    get() {
        if (isStable || possibleTypes.isEmpty()) return null
        return intersectWrappedTypes(possibleTypes + receiverValue.type)
    }

// with all smart casts if stable
val ReceiverValueWithSmartCastInfo.stableType: UnwrappedType
    get() {
        if (!isStable || possibleTypes.isEmpty()) return receiverValue.type.unwrap()
        return intersectWrappedTypes(possibleTypes + receiverValue.type)
    }

internal fun KotlinCallArgument.getExpectedType(parameter: ParameterDescriptor, languageVersionSettings: LanguageVersionSettings) =
    if (this.isSpread || this.isArrayAssignedAsNamedArgumentInAnnotation(parameter, languageVersionSettings)) {
        parameter.type.unwrap()
    } else {
        parameter.safeAs<ValueParameterDescriptor>()?.varargElementType?.unwrap() ?: parameter.type.unwrap()
    }

val ValueParameterDescriptor.isVararg: Boolean get() = varargElementType != null
val ParameterDescriptor.isVararg: Boolean get() = this.safeAs<ValueParameterDescriptor>()?.isVararg ?: false

/**
 * @return `true` iff the parameter has a default value, i.e. declares it, inherits it by overriding a parameter which has a default value,
 * or is a parameter of an 'actual' declaration, such that the corresponding 'expect' parameter has a default value.
 */
fun ValueParameterDescriptor.hasDefaultValue(): Boolean {
    return DFS.ifAny(
        listOf(this),
        { current -> current.overriddenDescriptors.map(ValueParameterDescriptor::getOriginal) },
        { it.declaresDefaultValue() || it.isActualParameterWithCorrespondingExpectedDefault }
    )
}

private fun ValueParameterDescriptor.checkExpectedParameter(checker: (ValueParameterDescriptor) -> Boolean): Boolean {
    val function = containingDeclaration
    if (function is FunctionDescriptor && function.isActual) {
        with(ExpectedActualResolver) {
            val expected = function.findCompatibleExpectedForActual(function.module).firstOrNull()
            return expected is FunctionDescriptor && checker(expected.valueParameters[index])
        }
    }
    return false
}

/**
 * The following two properties describe two different situations.
 *
 * Consider hierarchy:
 *
 *     expect open class A { fun foo(p: Int = 1) }
 *     expect open class B : A { fun foo(p: Int) }
 *
 *     actual open class A { actual fun foo(p: Int) }
 *     actual open class B : A { actual fun foo(p: Int) }
 *
 * For parameter `p` of method `foo`:
 * `isActualParameterWithAnyExpectedDefault` returns `true` for both actual A and B
 * `isActualParameterWithCorrespondingExpectedDefault` returns `true` for actual A, but `false` for actual B because expect B declaration
 *     doesn't have a default value
 */
val ValueParameterDescriptor.isActualParameterWithAnyExpectedDefault: Boolean
    get() = checkExpectedParameter { it.hasDefaultValue() }

/**
 * @see isActualParameterWithAnyExpectedDefault
 */
val ValueParameterDescriptor.isActualParameterWithCorrespondingExpectedDefault: Boolean
    get() = checkExpectedParameter { it.declaresDefaultValue() }

private fun KotlinCallArgument.isArrayAssignedAsNamedArgumentInAnnotation(
    parameter: ParameterDescriptor,
    languageVersionSettings: LanguageVersionSettings
): Boolean {
    if (!languageVersionSettings.supportsFeature(LanguageFeature.AssigningArraysToVarargsInNamedFormInAnnotations)) return false

    if (this.argumentName == null || !parameter.isVararg) return false

    return isParameterOfAnnotation(parameter) && this.isArrayOrArrayLiteral()
}

private fun KotlinCallArgument.isArrayOrArrayLiteral(): Boolean {
    if (this is CollectionLiteralKotlinCallArgument) return true
    if (this !is SimpleKotlinCallArgument) return false

    val type = this.receiver.receiverValue.type
    return KotlinBuiltIns.isArrayOrPrimitiveArray(type)
}
