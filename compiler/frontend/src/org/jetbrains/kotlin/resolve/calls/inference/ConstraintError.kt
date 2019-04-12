/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.constraintPosition.CompoundConstraintPosition
import org.jetbrains.kotlin.resolve.calls.inference.constraintPosition.ConstraintPosition

open class ConstraintError(val constraintPosition: ConstraintPosition)

class ParameterConstraintError(constraintPosition: ConstraintPosition) : ConstraintError(constraintPosition)

class ErrorInConstrainingType(constraintPosition: ConstraintPosition) : ConstraintError(constraintPosition)

class TypeInferenceError(constraintPosition: ConstraintPosition) : ConstraintError(constraintPosition)

class CannotCapture(constraintPosition: ConstraintPosition, val typeVariable: TypeVariable) : ConstraintError(constraintPosition)

fun newTypeInferenceOrParameterConstraintError(constraintPosition: ConstraintPosition) =
    if (constraintPosition.isParameter()) ParameterConstraintError(constraintPosition) else TypeInferenceError(constraintPosition)