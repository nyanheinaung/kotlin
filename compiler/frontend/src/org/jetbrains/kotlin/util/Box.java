/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util;

public class Box<T> {
    private final T data;

    public Box(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Override
    public int hashCode() {
        return super.hashCode(); // This class is needed to screen from calling data's hashCode()
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); // This class is needed to screen from calling data's equals()
    }
}
