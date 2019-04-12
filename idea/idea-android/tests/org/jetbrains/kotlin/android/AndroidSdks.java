/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android;


import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import org.jetbrains.android.sdk.AndroidSdkAdditionalData;
import org.jetbrains.android.sdk.AndroidSdkData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stub for com.android.tools.idea.sdk.AndroidSdks
 * stabbed to minimize changes in AndroidTestBase
 */
public class AndroidSdks {

    private static AndroidSdks INSTANCE;

    private AndroidSdkData mySdkData;

    private AndroidSdks() {

    }

    public static AndroidSdks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AndroidSdks();
        }

        return INSTANCE;
    }

    public AndroidSdkData tryToChooseAndroidSdk() {
        return mySdkData;
    }

    public void setSdkData(AndroidSdkData data) {
        mySdkData = data;
    }

    @Nullable
    public AndroidSdkAdditionalData getAndroidSdkAdditionalData(@NotNull Sdk sdk) {
        SdkAdditionalData data = sdk.getSdkAdditionalData();
        return data instanceof AndroidSdkAdditionalData ? (AndroidSdkAdditionalData)data : null;
    }
}
