/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import org.jetbrains.kotlin.checkers.TestCheckerUtil;
import org.jetbrains.kotlin.psi.KtFile;

public abstract class AbstractResolveTest extends ExtensibleResolveTestCase {
    @Override
    protected ExpectedResolveData getExpectedResolveData() {
        return new ExpectedResolveData(
                ExpectedResolveDataUtil.prepareDefaultNameToDescriptors(getEnvironment()),
                ExpectedResolveDataUtil.prepareDefaultNameToDeclaration(getEnvironment())
        ) {
            @Override
            protected KtFile createKtFile(String fileName, String text) {
                return TestCheckerUtil.createCheckAndReturnPsiFile(fileName, text, getProject());
            }
        };
    }
}
