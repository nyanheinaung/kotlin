/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.operation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.FunctionDescriptor;
import org.jetbrains.kotlin.js.backend.ast.JsExpression;
import org.jetbrains.kotlin.js.translate.callTranslator.CallInfo;
import org.jetbrains.kotlin.js.translate.callTranslator.CallInfoKt;
import org.jetbrains.kotlin.js.translate.context.TranslationContext;
import org.jetbrains.kotlin.js.translate.intrinsic.functions.basic.FunctionIntrinsic;
import org.jetbrains.kotlin.psi.KtUnaryExpression;
import org.jetbrains.kotlin.resolve.calls.callUtil.CallUtilKt;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;

import java.util.Collections;

public final class IntrinsicIncrementTranslator extends IncrementTranslator {
    @NotNull
    private final ResolvedCall<? extends FunctionDescriptor> resolvedCall;

    public IntrinsicIncrementTranslator(@NotNull KtUnaryExpression expression, @NotNull TranslationContext context) {
        super(expression, context);
        this.resolvedCall = CallUtilKt.getFunctionResolvedCallWithAssert(expression, context.bindingContext());
    }

    @Override
    @NotNull
    protected JsExpression operationExpression(@NotNull TranslationContext context, @NotNull JsExpression receiver) {
        FunctionIntrinsic intrinsic = context.intrinsics().getFunctionIntrinsic(resolvedCall.getResultingDescriptor(), context);
        assert intrinsic != null;
        CallInfo callInfo = CallInfoKt.getCallInfo(context, resolvedCall, receiver);
        return intrinsic.apply(callInfo, Collections.emptyList(), context);
    }
}
