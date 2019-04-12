/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.utils;

import org.jetbrains.kotlin.js.backend.ast.JsExpression;
import org.jetbrains.kotlin.js.backend.ast.JsFunction;
import org.jetbrains.kotlin.js.backend.ast.JsName;
import org.jetbrains.kotlin.js.backend.ast.JsNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.js.inline.util.CollectUtilsKt;

import java.util.Map;

import static org.jetbrains.kotlin.js.inline.util.CollectUtilsKt.collectNamedFunctions;

public class AstSearchUtil {
    @NotNull
    public static JsFunction getFunction(@NotNull JsNode searchRoot, String name) {
        JsFunction function = findByIdent(collectNamedFunctions(searchRoot), name);
        assert function != null: "Function `" + name + "` was not found";
        return function;
    }

    @NotNull
    public static JsExpression getMetadataOrFunction(@NotNull JsNode searchRoot, @NotNull String name) {
        JsExpression property = findByIdent(CollectUtilsKt.collectNamedFunctionsOrMetadata(searchRoot), name);
        assert property != null: "Property `" + name + "` was not found";
        return property;
    }

    @Nullable
    private static <T extends JsExpression> T findByIdent(@NotNull Map<JsName, T> properties, @NotNull String name) {
        for (Map.Entry<JsName, T> entry : properties.entrySet()) {
            if (entry.getKey().getIdent().equals(name)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
