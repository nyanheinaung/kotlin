/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.initializer;

import org.jetbrains.kotlin.js.backend.ast.JsExpression;
import org.jetbrains.kotlin.js.backend.ast.JsStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.PropertyDescriptor;
import org.jetbrains.kotlin.js.translate.context.Namer;
import org.jetbrains.kotlin.js.translate.context.TranslationContext;
import org.jetbrains.kotlin.js.translate.utils.JsAstUtils;
import org.jetbrains.kotlin.resolve.source.KotlinSourceElementKt;

import static org.jetbrains.kotlin.js.translate.utils.TranslationUtils.assignmentToBackingField;

public final class InitializerUtils {
    private InitializerUtils() {
    }

    @NotNull
    public static JsStatement generateInitializerForProperty(
            @NotNull TranslationContext context,
            @NotNull PropertyDescriptor descriptor,
            @NotNull JsExpression value
    ) {
        JsExpression assignment = assignmentToBackingField(context, descriptor, value);
        assignment.setSource(KotlinSourceElementKt.getPsi(descriptor.getSource()));
        return assignment.makeStmt();
    }

    @NotNull
    public static JsStatement generateInitializerForDelegate(
            @NotNull TranslationContext context,
            @NotNull PropertyDescriptor descriptor,
            @NotNull JsExpression value
    ) {
        return JsAstUtils.defineSimpleProperty(context.getNameForBackingField(descriptor), value, descriptor.getSource());
    }
}
