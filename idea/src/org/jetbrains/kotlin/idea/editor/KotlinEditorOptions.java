/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.editor;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
        name = "JetEditorOptions",
        storages = {
                @Storage(
                        file = "$APP_CONFIG$/editor.xml"
                )}
)
public class KotlinEditorOptions implements PersistentStateComponent<KotlinEditorOptions> {
    private boolean donTShowConversionDialog = false;
    private boolean enableJavaToKotlinConversion = true;

    public boolean isDonTShowConversionDialog() {
        return donTShowConversionDialog;
    }

    public void setDonTShowConversionDialog(boolean donTShowConversionDialog) {
        this.donTShowConversionDialog = donTShowConversionDialog;
    }

    public boolean isEnableJavaToKotlinConversion() {
        return enableJavaToKotlinConversion;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setEnableJavaToKotlinConversion(boolean enableJavaToKotlinConversion) {
        this.enableJavaToKotlinConversion = enableJavaToKotlinConversion;
    }

    @Override
    public KotlinEditorOptions getState() {
        return this;
    }

    @Override
    public void loadState(KotlinEditorOptions state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static KotlinEditorOptions getInstance() {
        return ServiceManager.getService(KotlinEditorOptions.class);
    }

    @Override
    @Nullable
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
