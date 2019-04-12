/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlinx.android.parcel

/**
 * Instructs the Kotlin compiler to generate `writeToParcel()`, `describeContents()` [android.os.Parcelable] methods,
 * as well as a `CREATOR` factory class automatically.
 *
 * The annotation is applicable only to classes that implements [android.os.Parcelable] (directly or indirectly).
 * Note that only the primary constructor properties will be serialized.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Parcelize