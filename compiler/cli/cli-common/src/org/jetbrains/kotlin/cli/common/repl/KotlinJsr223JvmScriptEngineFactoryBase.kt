/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl

import org.jetbrains.kotlin.config.KotlinCompilerVersion
import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory

abstract class KotlinJsr223JvmScriptEngineFactoryBase : ScriptEngineFactory {

    override fun getLanguageName(): String = "kotlin"
    override fun getLanguageVersion(): String = KotlinCompilerVersion.VERSION
    override fun getEngineName(): String = "kotlin"
    override fun getEngineVersion(): String = KotlinCompilerVersion.VERSION
    override fun getExtensions(): List<String> = listOf("kts")
    override fun getMimeTypes(): List<String> = listOf("text/x-kotlin")
    override fun getNames(): List<String> = listOf("kotlin")

    override fun getOutputStatement(toDisplay: String?): String = "print(\"$toDisplay\")"
    override fun getMethodCallSyntax(obj: String, m: String, vararg args: String): String = "$obj.$m(${args.joinToString()})"

    override fun getProgram(vararg statements: String): String {
        val sep = System.getProperty("line.separator")
        return statements.joinToString(sep) + sep
    }

    override fun getParameter(key: String?): Any? =
            when (key) {
                ScriptEngine.NAME -> engineName
                ScriptEngine.LANGUAGE -> languageName
                ScriptEngine.LANGUAGE_VERSION -> languageVersion
                ScriptEngine.ENGINE -> engineName
                ScriptEngine.ENGINE_VERSION -> engineVersion
                else -> null
            }
}
