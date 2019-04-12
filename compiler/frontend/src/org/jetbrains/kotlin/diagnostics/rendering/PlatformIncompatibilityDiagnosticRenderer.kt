/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics.rendering

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.resolve.multiplatform.ExpectedActualResolver.Compatibility.Incompatible

class PlatformIncompatibilityDiagnosticRenderer(
    private val mode: MultiplatformDiagnosticRenderingMode
) : DiagnosticParameterRenderer<Map<Incompatible, Collection<MemberDescriptor>>> {
    override fun render(
        obj: Map<Incompatible, Collection<MemberDescriptor>>,
        renderingContext: RenderingContext
    ): String {
        if (obj.isEmpty()) return ""

        return buildString {
            mode.newLine(this)
            renderIncompatibilityInformation(obj, "", renderingContext, mode)
        }
    }

    companion object {
        @JvmField
        val TEXT = PlatformIncompatibilityDiagnosticRenderer(MultiplatformDiagnosticRenderingMode())
    }
}

class IncompatibleExpectedActualClassScopesRenderer(
    private val mode: MultiplatformDiagnosticRenderingMode
) : DiagnosticParameterRenderer<List<Pair<MemberDescriptor, Map<Incompatible, Collection<MemberDescriptor>>>>> {
    override fun render(
        obj: List<Pair<MemberDescriptor, Map<Incompatible, Collection<MemberDescriptor>>>>,
        renderingContext: RenderingContext
    ): String {
        if (obj.isEmpty()) return ""

        return buildString {
            mode.newLine(this)
            renderIncompatibleClassScopes(obj, "", renderingContext, mode)
        }
    }

    companion object {
        @JvmField
        val TEXT = IncompatibleExpectedActualClassScopesRenderer(MultiplatformDiagnosticRenderingMode())
    }
}

open class MultiplatformDiagnosticRenderingMode {
    open fun newLine(sb: StringBuilder) {
        sb.appendln()
    }

    open fun renderList(sb: StringBuilder, elements: List<() -> Unit>) {
        sb.appendln()
        for (element in elements) {
            element()
        }
    }

    open fun renderDescriptor(sb: StringBuilder, descriptor: DeclarationDescriptor, context: RenderingContext, indent: String) {
        sb.append(indent)
        sb.append(INDENTATION_UNIT)
        sb.appendln(Renderers.COMPACT_WITH_MODIFIERS.render(descriptor, context))
    }
}

private fun StringBuilder.renderIncompatibilityInformation(
    map: Map<Incompatible, Collection<MemberDescriptor>>,
    indent: String,
    context: RenderingContext,
    mode: MultiplatformDiagnosticRenderingMode
) {
    for ((incompatibility, descriptors) in map) {
        append(indent)
        append("The following declaration")
        if (descriptors.size == 1) append(" is") else append("s are")
        append(" incompatible")
        incompatibility.reason?.let { append(" because $it") }
        append(":")

        mode.renderList(this, descriptors.map { descriptor ->
            { mode.renderDescriptor(this, descriptor, context, indent) }
        })

        if (incompatibility is Incompatible.ClassScopes) {
            append(indent)
            append("No actual members are found for expected members listed below:")
            mode.newLine(this)
            renderIncompatibleClassScopes(incompatibility.unfulfilled, indent, context, mode)
        }
    }
}

private fun StringBuilder.renderIncompatibleClassScopes(
    unfulfilled: List<Pair<MemberDescriptor, Map<Incompatible, Collection<MemberDescriptor>>>>,
    indent: String,
    context: RenderingContext,
    mode: MultiplatformDiagnosticRenderingMode
) {
    mode.renderList(this, unfulfilled.indices.map { index ->
        {
            val (descriptor, mapping) = unfulfilled[index]
            mode.renderDescriptor(this, descriptor, context, indent)
            if (mapping.isNotEmpty()) {
                mode.newLine(this)
                renderIncompatibilityInformation(mapping, indent + INDENTATION_UNIT, context, mode)
            }
            if (index != unfulfilled.lastIndex) {
                mode.newLine(this)
            }
        }
    })
}

private const val INDENTATION_UNIT = "    "
