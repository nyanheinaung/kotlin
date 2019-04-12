/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.common

import java.rmi.Remote
import java.rmi.RemoteException

interface RemoteInputStream : Remote {

    @Throws(RemoteException::class)
    fun close()

    @Throws(RemoteException::class)
    fun read(length: Int): ByteArray

    @Throws(RemoteException::class)
    fun read(): Int
}
