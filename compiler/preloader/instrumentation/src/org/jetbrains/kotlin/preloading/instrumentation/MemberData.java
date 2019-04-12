/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading.instrumentation;

class MemberData {

    private final String declaringClass;
    private final String name;
    private final String desc;

    public MemberData(String declaringClass, String name, String desc) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.desc = desc;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
