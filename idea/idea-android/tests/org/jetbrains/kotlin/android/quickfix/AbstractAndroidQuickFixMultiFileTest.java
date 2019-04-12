/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.quickfix;

import com.intellij.codeInsight.ImportFilter;
import com.intellij.facet.FacetManager;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.facet.impl.FacetUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.Extensions;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.quickfix.AbstractQuickFixMultiFileTest;
import org.jetbrains.kotlin.idea.test.KotlinTestImportFilter;

public abstract class AbstractAndroidQuickFixMultiFileTest extends AbstractQuickFixMultiFileTest {

    @Override
    protected void setUp() {
        super.setUp();
        addAndroidFacet();
        Extensions.getRootArea().getExtensionPoint(ImportFilter.EP_NAME).registerExtension(KotlinTestImportFilter.INSTANCE);
    }

    @Override
    protected void tearDown() {
        try {
            Extensions.getRootArea().getExtensionPoint(ImportFilter.EP_NAME).unregisterExtension(KotlinTestImportFilter.INSTANCE);
            AndroidFacet facet = FacetManager.getInstance(myModule).getFacetByType(AndroidFacet.getFacetType().getId());
            FacetUtil.deleteFacet(facet);
        } finally {
            super.tearDown();
        }
    }

    @Override
    protected void doTestWithExtraFile(@NotNull String beforeFileName) {
        addManifest();
        super.doTestWithExtraFile(beforeFileName);
    }

    private void addAndroidFacet() {
        FacetManager facetManager = FacetManager.getInstance(myModule);
        AndroidFacet facet = facetManager.createFacet(AndroidFacet.getFacetType(), "Android", null);

        ModifiableFacetModel facetModel = facetManager.createModifiableModel();
        facetModel.addFacet(facet);
        ApplicationManager.getApplication().runWriteAction(facetModel::commit);
    }

    private void addManifest() {
        myFixture.configureByFile("idea/testData/android/AndroidManifest.xml");
    }
}
