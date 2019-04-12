/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections.collections

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.getFunctionalClassKind
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.isSubclassOf
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

fun KotlinType.isFunctionOfAnyKind() = constructor.declarationDescriptor?.getFunctionalClassKind() != null

fun KotlinType?.isMap(builtIns: KotlinBuiltIns): Boolean {
    val classDescriptor = this?.constructor?.declarationDescriptor as? ClassDescriptor ?: return false
    return classDescriptor.name.asString().endsWith("Map") && classDescriptor.isSubclassOf(builtIns.map)
}

fun KotlinType?.isIterable(builtIns: KotlinBuiltIns): Boolean {
    val classDescriptor = this?.constructor?.declarationDescriptor as? ClassDescriptor ?: return false
    val className = classDescriptor.name.asString()
    // First two lines are just to make things faster
    return className.endsWith("List") && classDescriptor.isSubclassOf(builtIns.list)
            || className.endsWith("Set") && classDescriptor.isSubclassOf(builtIns.set)
            || classDescriptor.isSubclassOf(builtIns.iterable)
}

fun KtCallExpression.isCalling(fqName: FqName, context: BindingContext = analyze(BodyResolveMode.PARTIAL)): Boolean {
    return isCalling(listOf(fqName), context)
}

fun KtCallExpression.isCalling(fqNames: List<FqName>, context: BindingContext = analyze(BodyResolveMode.PARTIAL)): Boolean {
    val calleeText = calleeExpression?.text ?: return false
    val fqName = fqNames.firstOrNull { fqName -> fqName.asString().takeLastWhile { it != '.' } == calleeText } ?: return false
    return getResolvedCall(context)?.isCalling(fqName) == true
}

fun ResolvedCall<out CallableDescriptor>.isCalling(fqName: FqName): Boolean {
    return resultingDescriptor.fqNameSafe == fqName
}

fun ResolvedCall<*>.hasLastFunctionalParameterWithResult(context: BindingContext, predicate: (KotlinType) -> Boolean): Boolean {
    val lastParameter = resultingDescriptor.valueParameters.lastOrNull() ?: return false
    val lastArgument = valueArguments[lastParameter]?.arguments?.singleOrNull() ?: return false
    val functionalType = lastArgument.getArgumentExpression()?.getType(context) ?: return false
    // Both Function & KFunction must pass here
    if (!functionalType.isFunctionOfAnyKind()) return false
    val resultType = functionalType.arguments.lastOrNull()?.type ?: return false
    return predicate(resultType)
}