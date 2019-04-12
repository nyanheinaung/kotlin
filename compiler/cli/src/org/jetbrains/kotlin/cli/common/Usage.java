/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common;

import com.intellij.util.containers.ContainerUtil;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KCallable;
import kotlin.reflect.KClass;
import kotlin.reflect.KProperty1;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.cli.common.arguments.Argument;
import org.jetbrains.kotlin.cli.common.arguments.CommonToolArguments;
import org.jetbrains.kotlin.cli.common.arguments.ParseCommandLineArgumentsKt;
import org.jetbrains.kotlin.cli.common.arguments.PreprocessCommandLineArgumentsKt;

public class Usage {
    // The magic number 29 corresponds to the similar padding width in javac and scalac command line compilers
    private static final int OPTION_NAME_PADDING_WIDTH = 29;

    @NotNull
    public static <A extends CommonToolArguments> String render(@NotNull CLITool<A> tool, @NotNull A arguments) {
        boolean extraHelp = arguments.getExtraHelp();
        StringBuilder sb = new StringBuilder();
        appendln(sb, "Usage: " + tool.executableScriptFileName() + " <options> <source files>");
        appendln(sb, "where " + (extraHelp ? "advanced" : "possible") + " options include:");
        KClass<? extends CommonToolArguments> kClass = JvmClassMappingKt.getKotlinClass(arguments.getClass());
        for (KCallable<?> callable : kClass.getMembers()) {
            if (!(callable instanceof KProperty1)) continue;
            propertyUsage(sb, (KProperty1) callable, extraHelp);
        }

        if (extraHelp) {
            appendln(sb, "");
            appendln(sb, "Advanced options are non-standard and may be changed or removed without any notice.");
        }
        else {
            renderArgfileUsage(sb);
        }

        return sb.toString();
    }

    private static void propertyUsage(@NotNull StringBuilder sb, @NotNull KProperty1 property, boolean extraHelp) {
        Argument argument = ContainerUtil.findInstance(property.getAnnotations(), Argument.class);
        if (argument == null) return;

        if (extraHelp != ParseCommandLineArgumentsKt.isAdvanced(argument)) return;

        int startLength = sb.length();
        sb.append("  ");
        sb.append(argument.value());

        if (!argument.shortName().isEmpty()) {
            sb.append(" (");
            sb.append(argument.shortName());
            sb.append(")");
        }

        if (!argument.valueDescription().isEmpty()) {
            sb.append(ParseCommandLineArgumentsKt.isAdvanced(argument) ? "=" : " ");
            sb.append(argument.valueDescription());
        }

        int margin = startLength + OPTION_NAME_PADDING_WIDTH - 1;
        if (sb.length() >= margin + 5) { // Break the line if it's too long
            sb.append("\n");
            margin += sb.length() - startLength;
        }
        while (sb.length() < margin) {
            sb.append(" ");
        }

        sb.append(" ");
        appendln(sb, argument.description().replace("\n", "\n" + StringsKt.repeat(" ", OPTION_NAME_PADDING_WIDTH)));
    }

    private static void renderArgfileUsage(@NotNull StringBuilder sb) {
        int descriptionStart = sb.length() + OPTION_NAME_PADDING_WIDTH;
        sb.append("  ");
        sb.append(PreprocessCommandLineArgumentsKt.ARGFILE_ARGUMENT);
        sb.append("<argfile>");
        while (sb.length() < descriptionStart) {
            sb.append(" ");
        }
        appendln(sb, "Expand compiler arguments from the given file, containing one argument or file path per line");
    }

    private static void appendln(@NotNull StringBuilder sb, @NotNull String string) {
        sb.append(string);
        StringsKt.appendln(sb);
    }
}
