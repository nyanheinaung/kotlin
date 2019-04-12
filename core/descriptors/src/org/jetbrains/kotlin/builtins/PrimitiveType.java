/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.builtins;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum PrimitiveType {
    BOOLEAN("Boolean"),
    CHAR("Char"),
    BYTE("Byte"),
    SHORT("Short"),
    INT("Int"),
    FLOAT("Float"),
    LONG("Long"),
    DOUBLE("Double"),
    ;

    public static final Set<PrimitiveType> NUMBER_TYPES =
            Collections.unmodifiableSet(EnumSet.of(CHAR, BYTE, SHORT, INT, FLOAT, LONG, DOUBLE));

    private final Name typeName;
    private final Name arrayTypeName;
    private FqName typeFqName = null;
    private FqName arrayTypeFqName = null;

    private PrimitiveType(String typeName) {
        this.typeName = Name.identifier(typeName);
        this.arrayTypeName = Name.identifier(typeName + "Array");
    }

    @NotNull
    public Name getTypeName() {
        return typeName;
    }

    @NotNull
    public FqName getTypeFqName() {
        if (typeFqName != null)
            return typeFqName;

        typeFqName = KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME.child(typeName);
        return typeFqName;
    }

    @NotNull
    public Name getArrayTypeName() {
        return arrayTypeName;
    }

    @NotNull
    public FqName getArrayTypeFqName() {
        if (arrayTypeFqName != null)
            return arrayTypeFqName;

        arrayTypeFqName = KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME.child(arrayTypeName);
        return arrayTypeFqName;
    }
}
