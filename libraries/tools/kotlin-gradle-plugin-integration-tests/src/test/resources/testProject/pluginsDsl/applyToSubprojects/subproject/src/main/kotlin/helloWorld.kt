/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package demo

import sun.nio.cs.ext.Big5
import sun.net.spi.nameservice.dns.DNSNameService
import javax.crypto.Cipher
import com.sun.crypto.provider.SunJCE
import sun.nio.ByteBuffered

fun box(): String {
    val a = Big5() // charsets.jar
    val c = DNSNameService() // dnsns.ajr
    val e : Cipher? = null // jce.jar
    val f : SunJCE? = null // sunjce_provider.jar
    val j : ByteBuffered? = null // rt.jar
    return "OK"
}


