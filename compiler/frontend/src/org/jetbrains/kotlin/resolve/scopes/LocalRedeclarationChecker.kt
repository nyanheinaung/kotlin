/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.reportOnDeclarationOrFail
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.OverloadChecker


abstract class AbstractLocalRedeclarationChecker(val overloadChecker: OverloadChecker) : LocalRedeclarationChecker {
    override fun checkBeforeAddingToScope(scope: LexicalScope, newDescriptor: DeclarationDescriptor) {
        val name = newDescriptor.name
        val location = NoLookupLocation.WHEN_CHECK_DECLARATION_CONFLICTS
        when (newDescriptor) {
            is ClassifierDescriptor, is VariableDescriptor -> {
                val otherDescriptor = scope.getContributedClassifier(name, location)
                        ?: scope.getContributedVariables(name, location).firstOrNull()
                if (otherDescriptor != null) {
                    handleRedeclaration(otherDescriptor, newDescriptor)
                }
            }
            is FunctionDescriptor -> {
                val otherFunctions = scope.getContributedFunctions(name, location)
                val otherClass = scope.getContributedClassifier(name, location)
                val potentiallyConflictingOverloads =
                    if (otherClass is ClassDescriptor)
                        otherFunctions + otherClass.constructors
                    else
                        otherFunctions

                for (overloadedDescriptor in potentiallyConflictingOverloads) {
                    if (!overloadChecker.isOverloadable(overloadedDescriptor, newDescriptor)) {
                        handleConflictingOverloads(newDescriptor, overloadedDescriptor)
                        break
                    }
                }
            }
            else -> throw IllegalStateException("Unexpected type of descriptor: ${newDescriptor::class.java.name}, descriptor: $newDescriptor")
        }
    }

    protected abstract fun handleRedeclaration(first: DeclarationDescriptor, second: DeclarationDescriptor)
    protected abstract fun handleConflictingOverloads(first: CallableMemberDescriptor, second: CallableMemberDescriptor)
}

class ThrowingLocalRedeclarationChecker(overloadChecker: OverloadChecker) : AbstractLocalRedeclarationChecker(overloadChecker) {
    override fun handleRedeclaration(first: DeclarationDescriptor, second: DeclarationDescriptor) {
        throw IllegalStateException(
            String.format(
                "Redeclaration: %s (%s) and %s (%s) (no line info available)",
                DescriptorUtils.getFqName(first), first,
                DescriptorUtils.getFqName(second), second
            )
        )
    }

    override fun handleConflictingOverloads(first: CallableMemberDescriptor, second: CallableMemberDescriptor) {
        throw IllegalStateException(
            String.format(
                "Conflicting overloads: %s (%s) and %s (%s) (no line info available)",
                DescriptorUtils.getFqName(first), first,
                DescriptorUtils.getFqName(second), second
            )
        )
    }
}

class TraceBasedLocalRedeclarationChecker(val trace: BindingTrace, overloadChecker: OverloadChecker) :
    AbstractLocalRedeclarationChecker(overloadChecker) {
    override fun handleRedeclaration(first: DeclarationDescriptor, second: DeclarationDescriptor) {
        reportOnDeclarationOrFail(trace, first) { Errors.REDECLARATION.on(it, listOf(first, second)) }
        reportOnDeclarationOrFail(trace, second) { Errors.REDECLARATION.on(it, listOf(first, second)) }
    }

    override fun handleConflictingOverloads(first: CallableMemberDescriptor, second: CallableMemberDescriptor) {
        reportOnDeclarationOrFail(trace, first) { Errors.CONFLICTING_OVERLOADS.on(it, listOf(first, second)) }
        reportOnDeclarationOrFail(trace, second) { Errors.CONFLICTING_OVERLOADS.on(it, listOf(first, second)) }
    }
}