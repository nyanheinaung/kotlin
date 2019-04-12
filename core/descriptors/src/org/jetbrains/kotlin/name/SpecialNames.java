/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.name;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpecialNames {
    public static final Name NO_NAME_PROVIDED = Name.special("<no name provided>");
    public static final Name ROOT_PACKAGE = Name.special("<root package>");

    public static final Name DEFAULT_NAME_FOR_COMPANION_OBJECT = Name.identifier("Companion");

    // This name is used as a key for the case when something has no name _due to a syntactic error_
    // Example: fun (x: Int) = 5
    //          There's no name for this function in the PSI
    // The name contains a GUID to avoid clashes, if a clash happens, it's not a big deal: the code does not compile anyway
    public static final Name SAFE_IDENTIFIER_FOR_NO_NAME = Name.identifier("no_name_in_PSI_3d19d79d_1ba9_4cd0_b7f5_b46aa3cd5d40");

    public static final String ANONYMOUS = "<anonymous>";
    public static final Name ANONYMOUS_FUNCTION = Name.special(ANONYMOUS);

    @NotNull
    public static Name safeIdentifier(@Nullable Name name) {
        return name != null && !name.isSpecial() ? name : SAFE_IDENTIFIER_FOR_NO_NAME;
    }

    @NotNull
    public static Name safeIdentifier(@Nullable String name) {
        return safeIdentifier(name == null ? null : Name.identifier(name));
    }

    public static boolean isSafeIdentifier(@NotNull Name name) {
        return !name.asString().isEmpty() && !name.isSpecial();
    }

    private SpecialNames() {}
}
