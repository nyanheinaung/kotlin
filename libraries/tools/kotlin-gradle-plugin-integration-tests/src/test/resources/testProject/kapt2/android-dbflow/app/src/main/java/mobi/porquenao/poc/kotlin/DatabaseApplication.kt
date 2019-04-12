/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package mobi.porquenao.poc.kotlin

import android.app.Application
import com.raizlabs.android.dbflow.config.FlowConfig

import com.raizlabs.android.dbflow.config.FlowManager

class DatabaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FlowManager.init(FlowConfig.Builder(this).build())
    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }
}