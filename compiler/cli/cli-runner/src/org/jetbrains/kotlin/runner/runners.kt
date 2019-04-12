/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.runner

import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.Attributes
import java.util.jar.JarFile

class RunnerException(message: String) : RuntimeException(message)

abstract class AbstractRunner : Runner {
    protected abstract val className: String

    protected abstract fun createClassLoader(classpath: List<URL>): ClassLoader

    override fun run(classpath: List<URL>, arguments: List<String>) {
        val classLoader = createClassLoader(classpath)

        val mainClass = try {
            classLoader.loadClass(className)
        }
        catch (e: ClassNotFoundException) {
            throw RunnerException("could not find or load main class $className")
        }

        val main = try {
            mainClass.getDeclaredMethod("main", Array<String>::class.java)
        }
        catch (e: NoSuchMethodException) {
            throw RunnerException("'main' method not found in class $className")
        }

        if (!Modifier.isStatic(main.modifiers)) {
            throw RunnerException(
                    "'main' method of class $className is not static. " +
                    "Please ensure that 'main' is either a top level Kotlin function, " +
                    "a member function annotated with @JvmStatic, or a static Java method"
            )
        }

        try {
            main.invoke(null, arguments.toTypedArray())
        }
        catch (e: IllegalAccessException) {
            throw RunnerException("'main' method of class $className is not public")
        }
        catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }
}

class MainClassRunner(override val className: String) : AbstractRunner() {
    override fun createClassLoader(classpath: List<URL>): ClassLoader =
            URLClassLoader(classpath.toTypedArray(), null)
}

class JarRunner(private val path: String) : AbstractRunner() {
    override val className: String =
            try {
                val jar = JarFile(path)
                try {
                    jar.manifest.mainAttributes.getValue(Attributes.Name.MAIN_CLASS)
                }
                finally {
                    jar.close()
                }
            }
            catch (e: IOException) {
                throw RunnerException("could not read manifest from " + path + ": " + e.message)
            }
            ?: throw RunnerException("no Main-Class entry found in manifest in $path")

    override fun createClassLoader(classpath: List<URL>): ClassLoader {
        // 'kotlin *.jar' ignores the passed classpath as 'java -jar' does
        // TODO: warn on non-empty classpath?

        return URLClassLoader(arrayOf(File(path).toURI().toURL()), null)
    }
}

class ReplRunner : Runner {
    override fun run(classpath: List<URL>, arguments: List<String>) {
        // TODO: run REPL instead
        throw RunnerException("please specify at least one name or file to run")
    }
}

class ScriptRunner(private val path: String) : Runner {
    override fun run(classpath: List<URL>, arguments: List<String>) {
        // TODO
        throw RunnerException("running Kotlin scripts is not yet supported")
    }
}

class ExpressionRunner(private val code: String) : Runner {
    override fun run(classpath: List<URL>, arguments: List<String>) {
        // TODO
        throw RunnerException("evaluating expressions is not yet supported")
    }
}
