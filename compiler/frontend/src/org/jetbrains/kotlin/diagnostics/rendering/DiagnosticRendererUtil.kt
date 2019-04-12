/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics.rendering

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptorWithTypeParameters
import org.jetbrains.kotlin.renderer.DescriptorRenderer

fun <P : Any> renderParameter(parameter: P, renderer: DiagnosticParameterRenderer<P>?, context: RenderingContext): Any =
    renderer?.render(parameter, context) ?: parameter

fun ClassifierDescriptorWithTypeParameters.renderKindWithName(): String =
    DescriptorRenderer.getClassifierKindPrefix(this) + " '" + name + "'"

fun ClassDescriptor.renderKind(): String = DescriptorRenderer.getClassifierKindPrefix(this)

