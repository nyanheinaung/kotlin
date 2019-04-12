/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading.instrumentation;

public class MethodData extends MemberData {
    private final FieldData ownerField;
    private final int thisParameterIndex;
    private final int classNameParameterIndex;
    private final int methodNameParameterIndex;
    private final int methodDescParameterIndex;
    private final int allArgsParameterIndex;

    MethodData(
            FieldData ownerField,
            String declaringClass,
            String name,
            String desc,
            int thisParameterIndex,
            int classNameParameterIndex,
            int methodNameParameterIndex,
            int methodDescParameterIndex,
            int allArgsParameterIndex
    ) {
        super(declaringClass, name, desc);
        this.ownerField = ownerField;
        this.thisParameterIndex = thisParameterIndex;
        this.classNameParameterIndex = classNameParameterIndex;
        this.methodNameParameterIndex = methodNameParameterIndex;
        this.methodDescParameterIndex = methodDescParameterIndex;
        this.allArgsParameterIndex = allArgsParameterIndex;
    }

    public FieldData getOwnerField() {
        return ownerField;
    }

    public int getThisParameterIndex() {
        return thisParameterIndex;
    }

    public int getClassNameParameterIndex() {
        return classNameParameterIndex;
    }

    public int getMethodNameParameterIndex() {
        return methodNameParameterIndex;
    }

    public int getMethodDescParameterIndex() {
        return methodDescParameterIndex;
    }

    public int getAllArgsParameterIndex() {
        return allArgsParameterIndex;
    }
}
