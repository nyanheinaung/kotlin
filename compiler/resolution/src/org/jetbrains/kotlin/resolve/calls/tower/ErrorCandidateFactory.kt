/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tower

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.tower.WrongResolutionToClassifier.*
import org.jetbrains.kotlin.resolve.scopes.receivers.DetailedReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.QualifierReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.resolve.scopes.utils.findClassifier
import org.jetbrains.kotlin.utils.SmartList

enum class WrongResolutionToClassifier(val message: (Name) -> String) {
    TYPE_PARAMETER_AS_VALUE({ "Type parameter $it cannot be used as value" }),
    TYPE_PARAMETER_AS_FUNCTION({ "Type parameter $it cannot be called as function" }),
    INTERFACE_AS_VALUE({ "Interface $it does not have companion object" }),
    INTERFACE_AS_FUNCTION({ "Interface $it does not have constructors" }),
    EXPECT_CLASS_AS_FUNCTION({ "Expected class $it does not have default constructor" }),
    CLASS_AS_VALUE({ "Class $it does not have companion object" }),
    INNER_CLASS_CONSTRUCTOR_NO_RECEIVER({ "Constructor of inner class $it can be called only with receiver of containing class" }),
    OBJECT_AS_FUNCTION({ "Function 'invoke()' is not found in object $it" })
}

sealed class ErrorCandidate<out D : DeclarationDescriptor>(val descriptor: D) {
    class Classifier(
        classifierDescriptor: ClassifierDescriptor,
        val kind: WrongResolutionToClassifier
    ) : ErrorCandidate<ClassifierDescriptor>(classifierDescriptor) {
        val errorMessage = kind.message(descriptor.name)
    }
}

fun collectErrorCandidatesForFunction(
    scopeTower: ImplicitScopeTower,
    name: Name,
    explicitReceiver: DetailedReceiver?
): Collection<ErrorCandidate<*>> {
    val context = ErrorCandidateContext(scopeTower, name, explicitReceiver)
    context.asClassifierCall(asFunction = true)
    return context.result
}

fun collectErrorCandidatesForVariable(
    scopeTower: ImplicitScopeTower,
    name: Name,
    explicitReceiver: DetailedReceiver?
): Collection<ErrorCandidate<*>> {
    val context = ErrorCandidateContext(scopeTower, name, explicitReceiver)
    context.asClassifierCall(asFunction = false)
    return context.result
}

private class ErrorCandidateContext(
    val scopeTower: ImplicitScopeTower,
    val name: Name,
    val explicitReceiver: DetailedReceiver?
) {
    val result = SmartList<ErrorCandidate<*>>()

    fun add(errorCandidate: ErrorCandidate<*>) {
        result.add(errorCandidate)
    }
}

private fun ErrorCandidateContext.asClassifierCall(asFunction: Boolean) {
    val classifier =
        when (explicitReceiver) {
            is ReceiverValueWithSmartCastInfo -> {
                explicitReceiver.receiverValue.type.memberScope.getContributedClassifier(name, scopeTower.location)
            }
            is QualifierReceiver -> {
                explicitReceiver.staticScope.getContributedClassifier(name, scopeTower.location)
            }
            else -> scopeTower.lexicalScope.findClassifier(name, scopeTower.location)
        } ?: return

    val kind = getWrongResolutionToClassifier(classifier, asFunction) ?: return

    add(ErrorCandidate.Classifier(classifier, kind))
}

private fun ErrorCandidateContext.getWrongResolutionToClassifier(
    classifier: ClassifierDescriptor,
    asFunction: Boolean
): WrongResolutionToClassifier? =
    when (classifier) {
        is TypeAliasDescriptor -> classifier.classDescriptor?.let { getWrongResolutionToClassifier(it, asFunction) }

        is TypeParameterDescriptor -> if (asFunction) TYPE_PARAMETER_AS_FUNCTION else TYPE_PARAMETER_AS_VALUE

        is ClassDescriptor -> {
            when (classifier.kind) {
                ClassKind.INTERFACE -> if (asFunction) INTERFACE_AS_FUNCTION else INTERFACE_AS_VALUE

                ClassKind.OBJECT -> if (asFunction) OBJECT_AS_FUNCTION else null

                ClassKind.CLASS -> when {
                    asFunction && explicitReceiver is QualifierReceiver? && classifier.isInner -> INNER_CLASS_CONSTRUCTOR_NO_RECEIVER
                    asFunction && classifier.isExpect -> EXPECT_CLASS_AS_FUNCTION
                    !asFunction -> CLASS_AS_VALUE
                    else -> null
                }

                else -> null
            }
        }

        else -> null
    }
