/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test.testFramework

import com.intellij.mock.MockApplication
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.annotations.TestOnly
import java.lang.reflect.InvocationTargetException
import javax.swing.SwingUtilities

class EdtTestUtil {
    companion object {
        @TestOnly @JvmStatic fun runInEdtAndWait(runnable: Runnable) {
            org.jetbrains.kotlin.test.testFramework.runInEdtAndWait { runnable.run() }
        }
    }
}


// Test only because in production you must use Application.invokeAndWait(Runnable, ModalityState).
// The problem is - Application logs errors, but not throws. But in tests must be thrown.
// In any case name "runInEdtAndWait" is better than "invokeAndWait".
@TestOnly
fun runInEdtAndWait(runnable: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        runnable()
    }
    else {
        try {
            val application = ApplicationManager.getApplication()
                .takeIf { it !is MockApplication } // because MockApplication do nothing instead of `invokeAndWait`
            if (application != null)
                application.invokeAndWait(runnable)
            else
                SwingUtilities.invokeAndWait(runnable)
        }
        catch (e: InvocationTargetException) {
            throw e.cause ?: e
        }
    }
}