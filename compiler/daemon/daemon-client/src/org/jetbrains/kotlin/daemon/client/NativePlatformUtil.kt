/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.client

import org.jetbrains.kotlin.daemon.common.DaemonReportCategory
import java.io.IOException

private class NativePlatformLauncherWrapper {
    private val nativeLauncher: net.rubygrapefruit.platform.ProcessLauncher by lazy {
        net.rubygrapefruit.platform.Native.get(net.rubygrapefruit.platform.ProcessLauncher::class.java)
    }

    fun launch(processBuilder: ProcessBuilder): Process =
            try {
                nativeLauncher.start(processBuilder)
            }
            catch (e: net.rubygrapefruit.platform.NativeException) {
                throw IOException(e)
            }
}


fun launchProcessWithFallback(processBuilder: ProcessBuilder, reportingTargets: DaemonReportingTargets, reportingSource: String = "process launcher"): Process =
        try {
            // A separate class to delay classloading until this point, where we can catch class loading errors in case then the native lib is not in the classpath
            NativePlatformLauncherWrapper().launch(processBuilder)
        }
        catch (e: UnsatisfiedLinkError) {
            reportingTargets.report(DaemonReportCategory.DEBUG, "Could not start process with native process launcher, falling back to ProcessBuilder#start ($e)", reportingSource)
            null
        }
        catch (e: IOException) {
            reportingTargets.report(DaemonReportCategory.DEBUG, "Could not start process with native process launcher, falling back to ProcessBuilder#start (${e.cause})", reportingSource)
            null
        }
        catch (e: NoClassDefFoundError) {
            reportingTargets.report(DaemonReportCategory.DEBUG, "net.rubygrapefruit.platform library is not in the classpath, falling back to ProcessBuilder#start ($e)", reportingSource)
            null
        }
        catch (e: ClassNotFoundException) {
            reportingTargets.report(DaemonReportCategory.DEBUG, "net.rubygrapefruit.platform library is not in the classpath, falling back to ProcessBuilder#start ($e)", reportingSource)
            null
        }
        ?: processBuilder.start()
