/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.PrimitiveType;
import org.jetbrains.kotlin.name.FqName;

import java.util.*;

public enum JvmPrimitiveType {
    BOOLEAN(PrimitiveType.BOOLEAN, "boolean", "Z", "java.lang.Boolean"),
    CHAR(PrimitiveType.CHAR, "char", "C", "java.lang.Character"),
    BYTE(PrimitiveType.BYTE, "byte", "B", "java.lang.Byte"),
    SHORT(PrimitiveType.SHORT, "short", "S", "java.lang.Short"),
    INT(PrimitiveType.INT, "int", "I", "java.lang.Integer"),
    FLOAT(PrimitiveType.FLOAT, "float", "F", "java.lang.Float"),
    LONG(PrimitiveType.LONG, "long", "J", "java.lang.Long"),
    DOUBLE(PrimitiveType.DOUBLE, "double", "D", "java.lang.Double"),
    ;

    private static final Set<FqName> WRAPPERS_CLASS_NAMES;
    private static final Map<String, JvmPrimitiveType> TYPE_BY_NAME;
    private static final Map<PrimitiveType, JvmPrimitiveType> TYPE_BY_PRIMITIVE_TYPE;
    private static final Map<String, JvmPrimitiveType> TYPE_BY_DESC;

    static {
        WRAPPERS_CLASS_NAMES = new HashSet<FqName>();
        TYPE_BY_NAME = new HashMap<String, JvmPrimitiveType>();
        TYPE_BY_PRIMITIVE_TYPE = new EnumMap<PrimitiveType, JvmPrimitiveType>(PrimitiveType.class);
        TYPE_BY_DESC = new HashMap<String, JvmPrimitiveType>();

        for (JvmPrimitiveType type : values()) {
            WRAPPERS_CLASS_NAMES.add(type.getWrapperFqName());
            TYPE_BY_NAME.put(type.getJavaKeywordName(), type);
            TYPE_BY_PRIMITIVE_TYPE.put(type.getPrimitiveType(), type);
            TYPE_BY_DESC.put(type.getDesc(), type);
        }
    }

    public static boolean isWrapperClassName(@NotNull FqName className) {
        return WRAPPERS_CLASS_NAMES.contains(className);
    }

    @NotNull
    public static JvmPrimitiveType get(@NotNull String name) {
        JvmPrimitiveType result = TYPE_BY_NAME.get(name);
        if (result == null) {
            throw new AssertionError("Non-primitive type name passed: " + name);
        }
        return result;
    }

    @NotNull
    public static JvmPrimitiveType get(@NotNull PrimitiveType type) {
        return TYPE_BY_PRIMITIVE_TYPE.get(type);
    }

    @Nullable
    public static JvmPrimitiveType getByDesc(@NotNull String desc) {
        return TYPE_BY_DESC.get(desc);
    }

    private final PrimitiveType primitiveType;
    private final String name;
    private final String desc;
    private final FqName wrapperFqName;

    JvmPrimitiveType(@NotNull PrimitiveType primitiveType, @NotNull String name, @NotNull String desc, @NotNull String wrapperClassName) {
        this.primitiveType = primitiveType;
        this.name = name;
        this.desc = desc;
        this.wrapperFqName = new FqName(wrapperClassName);
    }

    @NotNull
    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    @NotNull
    public String getJavaKeywordName() {
        return name;
    }

    @NotNull
    public String getDesc() {
        return desc;
    }

    @NotNull
    public FqName getWrapperFqName() {
        return wrapperFqName;
    }
}
