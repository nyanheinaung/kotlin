/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

/**
 * This is an interface to show that {@link KtExpression} is not
 * actually an expression (in meaning that this expression can be placed after "val x = ").
 * This is possibly redundant interface, all inheritors of this interface should be refactored that they are not
 * {@link KtExpression}, after such refactoring, this interface can be removed.
 */
public interface KtStatementExpression {
}
