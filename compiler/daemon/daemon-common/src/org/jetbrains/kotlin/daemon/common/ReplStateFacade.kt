/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.common

import org.jetbrains.kotlin.cli.common.repl.ILineId
import java.rmi.Remote
import java.rmi.RemoteException

interface ReplStateFacade : Remote {

    @Throws(RemoteException::class)
    fun getId(): Int

    @Throws(RemoteException::class)
    fun getHistorySize(): Int

    @Throws(RemoteException::class)
    fun historyGet(index: Int): ILineId

    @Throws(RemoteException::class)
    fun historyReset(): List<ILineId>

    @Throws(RemoteException::class)
    fun historyResetTo(id: ILineId): List<ILineId>
}