/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused") // could be used externally in javax.script.ScriptEngineFactory META-INF file

package org.jetbrains.kotlin.script.jsr223

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.util.*
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine

class KotlinJsr223JvmLocalScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {

    override fun getScriptEngine(): ScriptEngine =
            KotlinJsr223JvmLocalScriptEngine(
                this,
                scriptCompilationClasspathFromContextOrStlib("kotlin-script-util.jar", wholeClasspath = true),
                KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
                arrayOf(Bindings::class)
            )
}

class KotlinJsr223JvmDaemonLocalEvalScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {

    override fun getScriptEngine(): ScriptEngine =
            KotlinJsr223JvmDaemonCompileScriptEngine(
                this,
                KotlinJars.compilerWithScriptingClasspath,
                scriptCompilationClasspathFromContextOrStlib("kotlin-script-util.jar", wholeClasspath = true),
                KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
                arrayOf(Bindings::class)
            )
}

