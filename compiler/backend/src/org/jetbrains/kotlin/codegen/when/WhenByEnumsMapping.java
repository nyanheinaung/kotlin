/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.when;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.resolve.constants.EnumValue;

import java.util.LinkedHashMap;
import java.util.Map;

public class WhenByEnumsMapping {
    public static final String MAPPING_ARRAY_FIELD_PREFIX = "$EnumSwitchMapping$";
    public static final String MAPPINGS_CLASS_NAME_POSTFIX = "$WhenMappings";

    private final Map<EnumValue, Integer> map = new LinkedHashMap<>();
    private final ClassDescriptor enumClassDescriptor;
    private final String outerClassInternalNameForExpression;
    private final String mappingsClassInternalName;
    private final int fieldNumber;

    public WhenByEnumsMapping(
            @NotNull ClassDescriptor enumClassDescriptor,
            @NotNull String outerClassInternalNameForExpression,
            int fieldNumber
    ) {
        this.enumClassDescriptor = enumClassDescriptor;
        this.outerClassInternalNameForExpression = outerClassInternalNameForExpression;
        this.mappingsClassInternalName = outerClassInternalNameForExpression + MAPPINGS_CLASS_NAME_POSTFIX;
        this.fieldNumber = fieldNumber;
    }

    public int getIndexByEntry(@NotNull EnumValue value) {
        Integer result = map.get(value);
        assert result != null : "entry " + value + " has no mapping";
        return result;
    }

    public void putFirstTime(@NotNull EnumValue value, int index) {
        if (!map.containsKey(value)) {
            map.put(value, index);
        }
    }

    public int size() {
        return map.size();
    }

    @NotNull
    public String getFieldName() {
        return MAPPING_ARRAY_FIELD_PREFIX + fieldNumber;
    }

    @NotNull
    public ClassDescriptor getEnumClassDescriptor() {
        return enumClassDescriptor;
    }

    @NotNull
    public String getOuterClassInternalNameForExpression() {
        return outerClassInternalNameForExpression;
    }

    @NotNull
    public String getMappingsClassInternalName() {
        return mappingsClassInternalName;
    }

    @NotNull
    public Iterable<Map.Entry<EnumValue, Integer>> enumValuesToIntMapping() {
        return map.entrySet();
    }
}
