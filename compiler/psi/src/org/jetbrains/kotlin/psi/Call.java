/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.resolve.scopes.receivers.Receiver;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;

import java.util.List;

public interface Call {

    // SAFE_ACCESS or DOT or so
    @Nullable
    ASTNode getCallOperationNode();

    default boolean isSemanticallyEquivalentToSafeCall() {
        return getCallOperationNode() != null && getCallOperationNode().getElementType() == KtTokens.SAFE_ACCESS;
    }

    @Nullable
    Receiver getExplicitReceiver();

    @Nullable
    ReceiverValue getDispatchReceiver();

    @Nullable
    KtExpression getCalleeExpression();

    @Nullable
    KtValueArgumentList getValueArgumentList();

    @ReadOnly
    @NotNull
    List<? extends ValueArgument> getValueArguments();

    @ReadOnly
    @NotNull
    List<? extends LambdaArgument> getFunctionLiteralArguments();

    @ReadOnly
    @NotNull
    List<KtTypeProjection> getTypeArguments();

    @Nullable
    KtTypeArgumentList getTypeArgumentList();

    @NotNull
    KtElement getCallElement();

    enum CallType {
        DEFAULT, ARRAY_GET_METHOD, ARRAY_SET_METHOD, INVOKE
    }

    @NotNull
    Call.CallType getCallType();
}
