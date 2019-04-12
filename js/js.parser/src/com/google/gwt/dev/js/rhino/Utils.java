/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.google.gwt.dev.js.rhino;

public class Utils {
    public static boolean isEndOfLine(int c) {
        return c == '\r' || c == '\n' || c == '\u2028' || c == '\u2029';
    }
}
