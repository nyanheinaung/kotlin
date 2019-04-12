/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.resolve.constants.*;
import org.jetbrains.kotlin.resolve.constants.StringValue;

public interface AnnotationArgumentVisitor<R, D> {
    R visitLongValue(@NotNull LongValue value, D data);

    R visitIntValue(IntValue value, D data);

    R visitErrorValue(ErrorValue value, D data);

    R visitShortValue(ShortValue value, D data);

    R visitByteValue(ByteValue value, D data);

    R visitDoubleValue(DoubleValue value, D data);

    R visitFloatValue(FloatValue value, D data);

    R visitBooleanValue(BooleanValue value, D data);

    R visitCharValue(CharValue value, D data);

    R visitStringValue(StringValue value, D data);

    R visitNullValue(NullValue value, D data);
    
    R visitEnumValue(EnumValue value, D data);
    
    R visitArrayValue(ArrayValue value, D data);

    R visitAnnotationValue(AnnotationValue value, D data);

    R visitKClassValue(KClassValue value, D data);

    R visitUByteValue(UByteValue value, D data);

    R visitUShortValue(UShortValue value, D data);

    R visitUIntValue(UIntValue value, D data);

    R visitULongValue(ULongValue value, D data);
}
