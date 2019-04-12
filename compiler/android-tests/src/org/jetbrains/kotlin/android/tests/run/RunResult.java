/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests.run;

public class RunResult {
    private final boolean status;
    private final String output;

    public RunResult(boolean ok, String output) {
        this.status = ok;
        this.output = output;
    }

    //true - ok
    //false - fail
    public boolean getStatus() {
        return status;
    }

    public String getOutput() {
        return output;
    }
}
