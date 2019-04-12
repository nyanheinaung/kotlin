/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlinx.android.extensions

import android.view.View

/**
 * A base interface for all view holders supporting Android Extensions-style view access.
 */
public interface LayoutContainer {
    /** Returns the root holder view. */
    public val containerView: View?
}