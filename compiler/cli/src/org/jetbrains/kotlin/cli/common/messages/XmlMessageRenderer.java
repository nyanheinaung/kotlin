/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XmlMessageRenderer implements MessageRenderer {
    @Override
    public String renderPreamble() {
        return "<MESSAGES>";
    }

    @Override
    public String render(@NotNull CompilerMessageSeverity severity, @NotNull String message, @Nullable CompilerMessageLocation location) {
        StringBuilder out = new StringBuilder();
        String tagName = severity.getPresentableName();
        out.append("<").append(tagName);
        if (location != null) {
            out.append(" path=\"").append(e(location.getPath())).append("\"");
            out.append(" line=\"").append(location.getLine()).append("\"");
            out.append(" column=\"").append(location.getColumn()).append("\"");
        }
        out.append(">");

        out.append(e(message));

        out.append("</").append(tagName).append(">\n");
        return out.toString();
    }

    private static String e(String str) {
        return StringUtil.escapeXml(str);
    }

    @Override
    public String renderUsage(@NotNull String usage) {
        return render(CompilerMessageSeverity.STRONG_WARNING, usage, null);
    }

    @Override
    public String renderConclusion() {
        return "</MESSAGES>";
    }

    @Override
    public String getName() {
        return "XML";
    }
}
