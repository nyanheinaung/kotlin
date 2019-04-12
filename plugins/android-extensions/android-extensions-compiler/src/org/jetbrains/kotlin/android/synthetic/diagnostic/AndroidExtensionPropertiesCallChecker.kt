/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.diagnostic

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.android.synthetic.descriptors.AndroidSyntheticPackageFragmentDescriptor
import org.jetbrains.kotlin.android.synthetic.diagnostic.ErrorsAndroid.*
import org.jetbrains.kotlin.android.synthetic.res.AndroidSyntheticProperty
import org.jetbrains.kotlin.android.synthetic.res.isErrorType
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.reportFromPlugin
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.isFlexible

class AndroidExtensionPropertiesCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        reportOn as? KtExpression ?: return

        val propertyDescriptor = resolvedCall.resultingDescriptor as? PropertyDescriptor ?: return
        val containingPackage = propertyDescriptor.containingDeclaration as? AndroidSyntheticPackageFragmentDescriptor ?: return
        val androidSyntheticProperty = propertyDescriptor as? AndroidSyntheticProperty ?: return

        with(context.trace) {
            checkUnresolvedWidgetType(reportOn, androidSyntheticProperty)
            checkDeprecated(reportOn, containingPackage)
            checkPartiallyDefinedResource(resolvedCall, androidSyntheticProperty, context)
        }
    }

    private fun DiagnosticSink.checkDeprecated(expression: KtExpression, packageDescriptor: AndroidSyntheticPackageFragmentDescriptor) {
        if (packageDescriptor.packageData.isDeprecated) {
            reportFromPlugin(SYNTHETIC_DEPRECATED_PACKAGE.on(expression), DefaultErrorMessagesAndroid)
        }
    }

    private fun DiagnosticSink.checkUnresolvedWidgetType(expression: KtExpression, property: AndroidSyntheticProperty) {
        if (!property.isErrorType) return
        val type = property.errorType ?: return

        val warning = if (type.contains('.')) SYNTHETIC_UNRESOLVED_WIDGET_TYPE else SYNTHETIC_INVALID_WIDGET_TYPE
        reportFromPlugin(warning.on(expression, type), DefaultErrorMessagesAndroid)
    }

    private fun DiagnosticSink.checkPartiallyDefinedResource(
            resolvedCall: ResolvedCall<*>,
            property: AndroidSyntheticProperty,
            context: CallCheckerContext
    ) {
        if (!property.resource.partiallyDefined) return
        val calleeExpression = resolvedCall.call.calleeExpression ?: return

        val expectedType = context.resolutionContext.expectedType
        if (!TypeUtils.noExpectedType(expectedType) && !expectedType.isMarkedNullable && !expectedType.isFlexible()) {
            reportFromPlugin(UNSAFE_CALL_ON_PARTIALLY_DEFINED_RESOURCE.on(calleeExpression), DefaultErrorMessagesAndroid)
            return
        }

        val outermostQualifiedExpression = findLeftOutermostQualifiedExpression(calleeExpression) ?: return
        val usage = outermostQualifiedExpression.parent

        if (usage is KtDotQualifiedExpression && usage.receiverExpression == outermostQualifiedExpression) {
            reportFromPlugin(UNSAFE_CALL_ON_PARTIALLY_DEFINED_RESOURCE.on(calleeExpression), DefaultErrorMessagesAndroid)
        }
    }

    private fun findLeftOutermostQualifiedExpression(calleeExpression: KtExpression?): KtElement? {
        val parent = calleeExpression?.parent ?: return null

        if (parent is KtQualifiedExpression && parent.selectorExpression == calleeExpression) {
            return findLeftOutermostQualifiedExpression(parent)
        }

        return calleeExpression
    }
}
