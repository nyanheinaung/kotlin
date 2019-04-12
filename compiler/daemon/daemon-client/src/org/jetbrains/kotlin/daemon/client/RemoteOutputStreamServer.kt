/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.client

import org.jetbrains.kotlin.daemon.common.LoopbackNetworkInterface
import org.jetbrains.kotlin.daemon.common.SOCKET_ANY_FREE_PORT
import org.jetbrains.kotlin.daemon.common.RemoteOutputStream
import org.jetbrains.kotlin.daemon.common.SOCKET_ANY_FREE_PORT
import java.io.OutputStream
import java.rmi.server.UnicastRemoteObject


class RemoteOutputStreamServer(val out: OutputStream, port: Int = SOCKET_ANY_FREE_PORT)
: RemoteOutputStream,
  UnicastRemoteObject(port, LoopbackNetworkInterface.clientLoopbackSocketFactory, LoopbackNetworkInterface.serverLoopbackSocketFactory)
{
    override fun close() {
        out.close()
    }

    override fun write(data: ByteArray, offset: Int, length: Int) {
        out.write(data, offset, length)
    }

    override fun write(dataByte: Int) {
        out.write(dataByte)
    }
}
