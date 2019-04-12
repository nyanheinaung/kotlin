/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ant

import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Task
import org.apache.tools.ant.types.Commandline
import org.apache.tools.ant.types.Path
import org.apache.tools.ant.types.Reference
import java.io.File
import java.io.PrintStream

abstract class KotlinCompilerBaseTask : Task() {
    protected abstract val compilerFqName: String

    val args: MutableList<String> = arrayListOf()

    var src: Path? = null
    var output: File? = null
    var nowarn: Boolean = false
    var verbose: Boolean = false
    var printVersion: Boolean = false
    var failOnError: Boolean = true

    var noStdlib: Boolean = false

    val additionalArguments: MutableList<Commandline.Argument> = arrayListOf()

    var exitCode: Int? = null

    fun createSrc(): Path {
        val srcPath = src
        if (srcPath == null) {
            val t = Path(getProject())
            src = t
            return t
        }

        return srcPath.createPath()
    }

    fun setSrcRef(ref: Reference) {
        createSrc().refid = ref
    }

    fun createCompilerArg(): Commandline.Argument {
        val argument = Commandline.Argument()
        additionalArguments.add(argument)
        return argument
    }

    abstract fun fillSpecificArguments()

    fun fillArguments() {
        val sourcePaths = src ?: throw BuildException("\"src\" should be specified")
        args.addAll(sourcePaths.list().map { File(it).canonicalPath })

        output ?: throw BuildException("\"output\" should be specified")

        if (nowarn) args.add("-nowarn")
        if (verbose) args.add("-verbose")
        if (printVersion) args.add("-version")

        args.addAll(additionalArguments.flatMap { it.parts.toList() })

        fillSpecificArguments()
    }

    final override fun execute() {
        fillArguments()

        val compilerClass = KotlinAntTaskUtil.getOrCreateClassLoader().loadClass(compilerFqName)
        val compiler = compilerClass.newInstance()
        val exec = compilerClass.getMethod("execFullPathsInMessages", PrintStream::class.java, Array<String>::class.java)

        log("Compiling ${src!!.list().toList()} => [${output!!.canonicalPath}]")

        val result = exec(compiler, System.err, args.toTypedArray())
        exitCode = (result as Enum<*>).ordinal

        if (failOnError && exitCode != 0) {
            throw BuildException("Compile failed; see the compiler error output for details.")
        }
    }
}
