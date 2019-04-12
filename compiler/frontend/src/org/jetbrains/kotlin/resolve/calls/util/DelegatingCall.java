/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.util;

import com.intellij.lang.ASTNode;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.resolve.scopes.receivers.Receiver;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;

import java.util.List;

public class DelegatingCall implements Call {

    private final Call delegate;

    public DelegatingCall(@NotNull Call delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public ASTNode getCallOperationNode() {
        return delegate.getCallOperationNode();
    }

    @Override
    @Nullable
    public Receiver getExplicitReceiver() {
        return delegate.getExplicitReceiver();
    }

    @Nullable
    @Override
    public ReceiverValue getDispatchReceiver() {
        return delegate.getDispatchReceiver();
    }

    @Override
    @Nullable
    public KtExpression getCalleeExpression() {
        return delegate.getCalleeExpression();
    }

    @Override
    @Nullable
    public KtValueArgumentList getValueArgumentList() {
        return delegate.getValueArgumentList();
    }

    @Override
    @NotNull
    @ReadOnly
    public List<? extends ValueArgument> getValueArguments() {
        return delegate.getValueArguments();
    }

    @Override
    @NotNull
    public List<? extends LambdaArgument> getFunctionLiteralArguments() {
        return delegate.getFunctionLiteralArguments();
    }

    @Override
    @NotNull
    public List<KtTypeProjection> getTypeArguments() {
        return delegate.getTypeArguments();
    }

    @Override
    @Nullable
    public KtTypeArgumentList getTypeArgumentList() {
        return delegate.getTypeArgumentList();
    }

    @NotNull
    @Override
    public KtElement getCallElement() {
        return delegate.getCallElement();
    }

    @NotNull
    @Override
    public CallType getCallType() {
        return delegate.getCallType();
    }

    @Override
    public String toString() {
        return "*" + delegate.toString();
    }
}
