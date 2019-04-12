/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.UnsignedTypes
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.DeclarationsChecker.Companion.hasAccessorImplementation
import org.jetbrains.kotlin.types.TypeUtils

object LateinitModifierApplicabilityChecker {
    fun checkLateinitModifierApplicability(trace: BindingTrace, ktDeclaration: KtCallableDeclaration, descriptor: VariableDescriptor) {
        val modifier = ktDeclaration.modifierList?.getModifier(KtTokens.LATEINIT_KEYWORD) ?: return

        val variables = when (descriptor) {
            is PropertyDescriptor -> "properties"
            is LocalVariableDescriptor -> "local variables"
            else -> throw AssertionError("Should be a property or a local variable: $descriptor")
        }

        val type = descriptor.type

        if (!descriptor.isVar) {
            trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is allowed only on mutable $variables"))
        }

        if (type.isInlineClassType()) {
            if (UnsignedTypes.isUnsignedType(type)) {
                trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on $variables of unsigned types"))
            } else {
                trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on $variables of inline class types"))
            }
        }

        if (type.isMarkedNullable) {
            trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on $variables of nullable types"))
        } else if (TypeUtils.isNullableType(type)) {
            trace.report(
                Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(
                    modifier,
                    "is not allowed on $variables of a type with nullable upper bound"
                )
            )
        }

        if (KotlinBuiltIns.isPrimitiveType(type)) {
            trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on $variables of primitive types"))
        }

        if (ktDeclaration is KtProperty) {
            if (ktDeclaration.hasDelegateExpression()) {
                trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on delegated properties"))
            } else if (ktDeclaration.hasInitializer()) {
                trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on $variables with initializer"))
            }
        }

        if (descriptor is PropertyDescriptor) {
            val isAbstract = descriptor.modality == Modality.ABSTRACT
            val hasDelegateExpressionOrInitializer = ktDeclaration is KtProperty && ktDeclaration.hasDelegateExpressionOrInitializer()
            val hasAccessorImplementation = descriptor.hasAccessorImplementation()
            val hasBackingField = trace.bindingContext.get(BindingContext.BACKING_FIELD_REQUIRED, descriptor) ?: false

            if (ktDeclaration is KtParameter) {
                trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on primary constructor parameters"))
            }

            if (isAbstract) {
                trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on abstract properties"))
            }

            if (!hasDelegateExpressionOrInitializer) {
                if (hasAccessorImplementation) {
                    trace.report(
                        Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(
                            modifier,
                            "is not allowed on properties with a custom getter or setter"
                        )
                    )
                } else if (!isAbstract && !hasBackingField) {
                    trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on properties without backing field"))
                }
            }

            if (descriptor.extensionReceiverParameter != null) {
                trace.report(Errors.INAPPLICABLE_LATEINIT_MODIFIER.on(modifier, "is not allowed on extension properties"))
            }
        }
    }
}