/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.when;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.ExpressionCodegen;
import org.jetbrains.kotlin.psi.KtWhenExpression;
import org.jetbrains.kotlin.resolve.constants.ConstantValue;
import org.jetbrains.kotlin.resolve.constants.EnumValue;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.Type;

public class EnumSwitchCodegen extends SwitchCodegen {
    private final WhenByEnumsMapping mapping;

    public EnumSwitchCodegen(
            @NotNull KtWhenExpression expression,
            boolean isStatement,
            boolean isExhaustive,
            @NotNull ExpressionCodegen codegen,
            @NotNull WhenByEnumsMapping mapping
    ) {
        super(expression, isStatement, isExhaustive, codegen, codegen.getState().getTypeMapper().mapType(mapping.getEnumClassDescriptor()));
        this.mapping = mapping;
    }

    @Override
    protected void generateSubjectValueToIndex() {
        codegen.getState().getMappingsClassesForWhenByEnum().generateMappingsClassForExpression(expression);

        generateNullCheckIfNeeded();

        v.getstatic(
                mapping.getMappingsClassInternalName(),
                mapping.getFieldName(),
                MappingClassesForWhenByEnumCodegen.MAPPINGS_FIELD_DESCRIPTOR
        );

        v.swap();

        Type enumType = codegen.getState().getTypeMapper().mapClass(mapping.getEnumClassDescriptor());
        v.invokevirtual(enumType.getInternalName(), "ordinal", Type.getMethodDescriptor(Type.INT_TYPE), false);
        v.aload(Type.INT_TYPE);
    }

    @Override
    protected void processConstant(@NotNull ConstantValue<?> constant, @NotNull Label entryLabel) {
        assert constant instanceof EnumValue : "guaranteed by usage contract";
        putTransitionOnce(mapping.getIndexByEntry((EnumValue) constant), entryLabel);
    }
}
