/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tasks;

public enum ExplicitReceiverKind {
    EXTENSION_RECEIVER,
    DISPATCH_RECEIVER,
    NO_EXPLICIT_RECEIVER,

    // A very special case.
    // In a call 'b.foo(1)' where class 'Foo' has an extension member 'fun B.invoke(Int)' function 'invoke' has two explicit receivers:
    // 'b' (as extension receiver) and 'foo' (as dispatch receiver).
    BOTH_RECEIVERS;

    public boolean isExtensionReceiver() {
        return this == EXTENSION_RECEIVER || this == BOTH_RECEIVERS;
    }

    public boolean isDispatchReceiver() {
        return this == DISPATCH_RECEIVER || this == BOTH_RECEIVERS;
    }
}
