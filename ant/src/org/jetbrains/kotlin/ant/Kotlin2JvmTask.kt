/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ant

import org.apache.tools.ant.types.Path
import org.apache.tools.ant.types.Reference
import java.io.File.pathSeparator

class Kotlin2JvmTask : KotlinCompilerBaseTask() {
    override val compilerFqName = "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler"

    var includeRuntime: Boolean = true
    var moduleName: String? = null

    var noReflect: Boolean = false

    private var compileClasspath: Path? = null

    fun setClasspath(classpath: Path) {
        if (compileClasspath == null) {
            compileClasspath = classpath
        }
        else {
            compileClasspath!!.append(classpath)
        }
    }

    fun setClasspathRef(ref: Reference) {
        if (compileClasspath == null) {
            compileClasspath = Path(getProject())
        }
        compileClasspath!!.createPath().refid = ref
    }

    fun addConfiguredClasspath(classpath: Path) {
        setClasspath(classpath)
    }

    override fun fillSpecificArguments() {
        args.add("-d")
        args.add(output!!.canonicalPath)

        compileClasspath?.let {
            args.add("-classpath")
            args.add(it.list().joinToString(pathSeparator))
        }


        if (moduleName == null) {
            moduleName = defaultModuleName
        }

        moduleName?.let {
            args.add("-module-name")
            args.add(moduleName!!)
        }

        if (noStdlib) args.add("-no-stdlib")
        if (noReflect) args.add("-no-reflect")
        if (includeRuntime) args.add("-include-runtime")
    }
}
