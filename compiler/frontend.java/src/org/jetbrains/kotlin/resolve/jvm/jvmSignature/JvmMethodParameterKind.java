/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.jvmSignature;

public enum JvmMethodParameterKind {
    VALUE,
    THIS,
    OUTER,
    RECEIVER,
    CAPTURED_LOCAL_VARIABLE,
    ENUM_NAME_OR_ORDINAL,
    SUPER_CALL_PARAM,
    CONSTRUCTOR_MARKER;

    public boolean isSkippedInGenericSignature() {
        return this == OUTER || this == ENUM_NAME_OR_ORDINAL;
    }
}
