/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading.instrumentation;

import org.jetbrains.org.objectweb.asm.Type;

class FieldData extends MemberData {

    private final Type runtimeType;

    public FieldData(String declaringClass, String name, String desc, Type runtimeType) {
        super(declaringClass, name, desc);
        this.runtimeType = runtimeType;
    }

    public Type getRuntimeType() {
        return runtimeType;
    }
}
