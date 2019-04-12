/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.client

import org.jetbrains.kotlin.daemon.common.LoopbackNetworkInterface
import org.jetbrains.kotlin.daemon.common.RemoteInputStream
import org.jetbrains.kotlin.daemon.common.SOCKET_ANY_FREE_PORT
import java.io.InputStream
import java.rmi.server.UnicastRemoteObject


class RemoteInputStreamServer(val `in`: InputStream, port: Int = SOCKET_ANY_FREE_PORT)
: RemoteInputStream,
  UnicastRemoteObject(port, LoopbackNetworkInterface.clientLoopbackSocketFactory, LoopbackNetworkInterface.serverLoopbackSocketFactory)
{
    override fun close() {
        `in`.close()
    }

    override fun read(length: Int): ByteArray {
        val buf = ByteArray(length)
        val readBytes = `in`.read(buf, 0, length)
        return if (readBytes == length) buf
               else buf.copyOfRange(0, readBytes)
    }

    override fun read(): Int =
            `in`.read()
}
