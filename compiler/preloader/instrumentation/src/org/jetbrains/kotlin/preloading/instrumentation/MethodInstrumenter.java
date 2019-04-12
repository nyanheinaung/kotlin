/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading.instrumentation;

import java.util.List;
import java.util.regex.Pattern;

class MethodInstrumenter {
    private final String debugName;
    private final Pattern classPattern;
    private final Pattern namePattern;
    private final Pattern descPattern;
    private final boolean allowMultipleMatches;
    private final List<MethodData> enterData;
    private final List<MethodData> normalReturnData;
    private final List<MethodData> exceptionData;
    private final boolean logApplications;
    private final boolean dumpByteCode;

    public MethodInstrumenter(
            String debugName,
            Pattern classPattern, Pattern namePattern,
            Pattern descPattern,
            boolean allowMultipleMatches,
            List<MethodData> enterData,
            List<MethodData> normalReturnData,
            List<MethodData> exceptionData,
            boolean logApplications,
            boolean dumpByteCode
    ) {
        this.debugName = debugName;
        this.classPattern = classPattern;
        this.namePattern = namePattern;
        this.descPattern = descPattern;
        this.allowMultipleMatches = allowMultipleMatches;
        this.enterData = enterData;
        this.normalReturnData = normalReturnData;
        this.exceptionData = exceptionData;
        this.logApplications = logApplications;
        this.dumpByteCode = dumpByteCode;
    }

    public boolean allowsMultipleMatches() {
        return allowMultipleMatches;
    }

    public void reportApplication(String className, String methodName, String methodDesc) {
        if (logApplications) {
            System.out.println(toString() + " applied to " + className + ":" + methodName + methodDesc);
        }
    }

    public boolean isApplicable(String name, String desc) {
        return namePattern.matcher(name).matches() && descPattern.matcher(desc).matches();
    }

    public List<MethodData> getEnterData() {
        return enterData;
    }

    public List<MethodData> getNormalReturnData() {
        return normalReturnData;
    }

    public List<MethodData> getExceptionData() {
        return exceptionData;
    }

    boolean shouldDumpByteCode() {
        return dumpByteCode;
    }

    @Override
    public String toString() {
        return debugName + "[" + classPattern + ":" + namePattern + " " + descPattern + (allowMultipleMatches ? " multiple" : "") + "]";
    }
}
