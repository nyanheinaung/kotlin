/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example.kt15001

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MyModule(private val context: Context) {
  @Provides @Singleton fun provideContext() = context

  override fun hashCode(): Int {
    return super.hashCode()
  }
}
