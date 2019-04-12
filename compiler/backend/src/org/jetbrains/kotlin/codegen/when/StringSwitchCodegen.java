/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.when;

import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.ExpressionCodegen;
import org.jetbrains.kotlin.psi.KtWhenExpression;
import org.jetbrains.kotlin.resolve.constants.ConstantValue;
import org.jetbrains.kotlin.resolve.constants.StringValue;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringSwitchCodegen extends SwitchCodegen {
    private static final String HASH_CODE_METHOD_DESC = Type.getMethodDescriptor(Type.INT_TYPE);
    private static final String EQUALS_METHOD_DESC = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(Object.class));

    private final Map<Integer, List<Pair<String, Label>>> hashCodesToStringAndEntryLabel = new HashMap<>();
    private int tempVarIndex;

    public StringSwitchCodegen(
            @NotNull KtWhenExpression expression,
            boolean isStatement,
            boolean isExhaustive,
            @NotNull ExpressionCodegen codegen
    ) {
        super(expression, isStatement, isExhaustive, codegen, null);
    }

    @Override
    protected void processConstant(
            @NotNull ConstantValue<?> constant, @NotNull Label entryLabel
    ) {
        assert constant instanceof StringValue : "guaranteed by usage contract";
        int hashCode = constant.hashCode();

        if (!transitionsTable.containsKey(hashCode)) {
            transitionsTable.put(hashCode, new Label());
            hashCodesToStringAndEntryLabel.put(hashCode, new ArrayList<>());
        }

        hashCodesToStringAndEntryLabel.get(hashCode).add(new Pair<>(((StringValue) constant).getValue(), entryLabel));
    }

    @Override
    public void generate() {
        super.generate();
        codegen.myFrameMap.leaveTemp(subjectType);
    }

    @Override
    protected void generateSubjectValueToIndex() {
        generateNullCheckIfNeeded();

        tempVarIndex = codegen.myFrameMap.enterTemp(subjectType);
        v.store(tempVarIndex, subjectType);
        v.load(tempVarIndex, subjectType);

        v.invokevirtual(
                subjectType.getInternalName(),
                "hashCode", HASH_CODE_METHOD_DESC, false
        );
    }

    @Override
    protected void generateEntries() {
        for (int hashCode : hashCodesToStringAndEntryLabel.keySet()) {
            v.visitLabel(transitionsTable.get(hashCode));

            List<Pair<String, Label>> items = hashCodesToStringAndEntryLabel.get(hashCode);
            Label nextLabel = null;

            for (int i = 0; i < items.size(); i++) {
                if (nextLabel != null) {
                    v.visitLabel(nextLabel);
                }

                Pair<String, Label> stringAndEntryLabel = items.get(i);

                v.load(tempVarIndex, subjectType);
                v.aconst(stringAndEntryLabel.first);
                v.invokevirtual(
                        subjectType.getInternalName(),
                        "equals",
                        EQUALS_METHOD_DESC,
                        false
                );

                if (i + 1 < items.size()) {
                    nextLabel = new Label();
                }
                else {
                    nextLabel = defaultLabel;
                }

                v.ifeq(nextLabel);
                v.goTo(stringAndEntryLabel.getSecond());
            }
        }

        super.generateEntries();
    }
}
