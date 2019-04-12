/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test.testFramework;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.ui.Queryable;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Based on com.intellij.testFramework.PlatformTestUtil
public class KtPlatformTestUtil {
    @NotNull
    public static String getTestName(@NotNull String name, boolean lowercaseFirstLetter) {
        name = StringUtil.trimStart(name, "test");
        return StringUtil.isEmpty(name) ? "" : lowercaseFirstLetter(name, lowercaseFirstLetter);
    }

    @NotNull
    public static String lowercaseFirstLetter(@NotNull String name, boolean lowercaseFirstLetter) {
        if (lowercaseFirstLetter && !isAllUppercaseName(name)) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    public static boolean isAllUppercaseName(@NotNull String name) {
        int uppercaseChars = 0;
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLowerCase(name.charAt(i))) {
                return false;
            }
            if (Character.isUpperCase(name.charAt(i))) {
                uppercaseChars++;
            }
        }
        return uppercaseChars >= 3;
    }

    @Nullable
    protected static String toString(@Nullable Object node, @Nullable Queryable.PrintInfo printInfo) {
        if (node instanceof AbstractTreeNode) {
            if (printInfo != null) {
                return ((AbstractTreeNode) node).toTestString(printInfo);
            }
            else {
                @SuppressWarnings({"deprecation", "UnnecessaryLocalVariable"}) String presentation =
                        ((AbstractTreeNode) node).getTestPresentation();
                return presentation;
            }
        }
        if (node == null) {
            return "NULL";
        }
        return node.toString();
    }
}