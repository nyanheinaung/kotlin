/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.typeUtil.builtIns
import org.jetbrains.kotlin.types.typeUtil.contains
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

object KClassWithIncorrectTypeArgumentChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is CallableMemberDescriptor || descriptor.visibility == Visibilities.LOCAL) return

        if (declaration !is KtCallableDeclaration || declaration.typeReference != null) return

        // prevent duplicate reporting
        if (descriptor is PropertyAccessorDescriptor) return

        val returnType = descriptor.returnType ?: return

        var typeParameterWithoutNotNullableUpperBound: TypeParameterDescriptor? = null
        returnType.contains { type ->
            val kClassWithBadArgument = type.isKClassWithBadArgument()
            if (kClassWithBadArgument) {
                type.arguments.singleOrNull()?.type?.constructor?.declarationDescriptor?.let {
                    if (it is TypeParameterDescriptor && it.containingDeclaration == descriptor) {
                        typeParameterWithoutNotNullableUpperBound = it
                    }
                }
            }
            kClassWithBadArgument
        }

        if (typeParameterWithoutNotNullableUpperBound != null) {
            context.trace.report(
                Errors.KCLASS_WITH_NULLABLE_TYPE_PARAMETER_IN_SIGNATURE.on(declaration, typeParameterWithoutNotNullableUpperBound!!)
            )
        }
    }

    private fun UnwrappedType.isKClassWithBadArgument(): Boolean {
        val argumentType = arguments.singleOrNull()?.let { if (it.isStarProjection) null else it.type.unwrap() } ?: return false
        val klass = constructor.declarationDescriptor as? ClassDescriptor ?: return false

        return KotlinBuiltIns.isKClass(klass) && !argumentType.isSubtypeOf(argumentType.builtIns.anyType)
    }
}
