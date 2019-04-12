/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac

import com.sun.tools.javac.main.Option
import com.sun.tools.javac.util.Options
import java.util.regex.Pattern

object JavacOptionsMapper {

    fun map(options: Options, arguments: List<String>) {
        arguments.forEach { options.putOption(it) }
    }

    fun setUTF8Encoding(options: Options) = options.put(Option.ENCODING, "UTF8")

    private val optionPattern = Pattern.compile("\\s+")

    private fun Options.putOption(option: String) =
            option.split(optionPattern)
            .filter { it.isNotEmpty() }
            .let { arg ->
                when(arg.size) {
                    1 -> put(arg[0], arg[0])
                    2 -> put(arg[0], arg[1])
                }
            }

}