/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fileClasses;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.name.FqName;

public final class OldPackageFacadeClassUtils {
    private static final String PACKAGE_CLASS_NAME_SUFFIX = "Package";
    private static final String DEFAULT_PACKAGE_CLASS_NAME = "_Default" + PACKAGE_CLASS_NAME_SUFFIX;

    private OldPackageFacadeClassUtils() {
    }

    // ex. <root> -> _DefaultPackage, a -> APackage, a.b -> BPackage
    @NotNull
    public static String getPackageClassName(@NotNull FqName packageFQN) {
        if (packageFQN.isRoot()) {
            return DEFAULT_PACKAGE_CLASS_NAME;
        }
        return capitalizeNonEmptyString(packageFQN.shortName().asString()) + PACKAGE_CLASS_NAME_SUFFIX;
    }

    @NotNull
    private static String capitalizeNonEmptyString(@NotNull String s) {
        return Character.isUpperCase(s.charAt(0)) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
