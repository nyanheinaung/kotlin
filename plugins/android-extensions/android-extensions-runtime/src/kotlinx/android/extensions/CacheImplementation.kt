/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlinx.android.extensions

/**
 * Caching mechanism for [LayoutContainer] implementations, and also for the types directly supported by Android Extensions,
 * such as [android.app.Activity] or [android.app.Fragment].
 */
public enum class CacheImplementation {
    /** Use [android.util.SparseArray] as a backing store for the resolved views. */
    SPARSE_ARRAY,
    /** Use [HashMap] as a backing store for the resolved views (default). */
    HASH_MAP,
    /** Do not cache views for this layout. */
    NO_CACHE;

    companion object {
        /** The default cache implementation is [HASH_MAP]. */
        val DEFAULT = HASH_MAP
    }
}