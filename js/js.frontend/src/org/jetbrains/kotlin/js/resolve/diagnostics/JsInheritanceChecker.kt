/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.builtins.isBuiltinFunctionalTypeOrSubtype
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.calls.components.hasDefaultValue
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyExternal
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.typeUtil.immediateSupertypes

object JsInheritanceChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor is FunctionDescriptor && !descriptor.isEffectivelyExternal() &&
            isOverridingExternalWithOptionalParams(descriptor)
        ) {
            context.trace.report(ErrorsJs.OVERRIDING_EXTERNAL_FUN_WITH_OPTIONAL_PARAMS.on(declaration))
        }
        else if (descriptor is ClassDescriptor && !descriptor.isEffectivelyExternal()) {
            val fakeOverriddenMethod = findFakeMethodOverridingExternalWithOptionalParams(descriptor)
            if (fakeOverriddenMethod != null) {
                context.trace.report(ErrorsJs.OVERRIDING_EXTERNAL_FUN_WITH_OPTIONAL_PARAMS_WITH_FAKE.on(declaration, fakeOverriddenMethod))
            }
        }

        if (descriptor is ClassDescriptor &&
            descriptor.defaultType.immediateSupertypes().any { it.isBuiltinFunctionalTypeOrSubtype }
        ) {
            context.trace.report(ErrorsJs.IMPLEMENTING_FUNCTION_INTERFACE.on(declaration as KtClassOrObject))
        }
    }

    private fun isOverridingExternalWithOptionalParams(function: FunctionDescriptor): Boolean {
        if (!function.kind.isReal && function.modality == Modality.ABSTRACT) return false

        for (overriddenFunction in function.overriddenDescriptors.filter { it.isEffectivelyExternal() }) {
            if (overriddenFunction.valueParameters.any { it.hasDefaultValue() }) return true
        }

        return false
    }

    private fun findFakeMethodOverridingExternalWithOptionalParams(cls: ClassDescriptor): FunctionDescriptor? {
        val members = cls.unsubstitutedMemberScope.getContributedDescriptors(DescriptorKindFilter.CALLABLES)
                .mapNotNull { it as? FunctionDescriptor }
                .filter { it.containingDeclaration == cls && !it.kind.isReal && it.overriddenDescriptors.size > 1 }

        return members.firstOrNull { isOverridingExternalWithOptionalParams(it) }
    }
}
