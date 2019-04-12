/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ant

import org.apache.tools.ant.types.Path
import java.io.File

class Kotlin2JsTask : KotlinCompilerBaseTask() {
    override val compilerFqName = "org.jetbrains.kotlin.cli.js.K2JSCompiler"

    var libraries: Path? = null
    var outputPrefix: File? = null
    var outputPostfix: File? = null
    var sourceMap: Boolean = false
    var metaInfo: Boolean = false
    var moduleKind: String = "plain"

    /**
     * {@link K2JsArgumentConstants.CALL} (default) if need generate a main function call (main function will be auto detected)
     * {@link K2JsArgumentConstants.NO_CALL} otherwise.
     */
    var main: String? = null

    fun createLibraries(): Path {
        val libraryPaths = libraries ?: return Path(getProject()).also { libraries = it }
        return libraryPaths.createPath()
    }

    override fun fillSpecificArguments() {
        args.add("-output")
        args.add(output!!.canonicalPath)

        // TODO: write test
        libraries?.let {
            args.add("-libraries")
            args.add(it.list().joinToString(File.pathSeparator) { File(it).canonicalPath })
        }

        outputPrefix?.let {
            args.add("-output-prefix")
            args.add(it.canonicalPath)
        }

        outputPostfix?.let {
            args.add("-output-postfix")
            args.add(it.canonicalPath)
        }

        main?.let {
            args.add("-main")
            args.add(it)
        }

        if (noStdlib) args.add("-no-stdlib")
        if (sourceMap) args.add("-source-map")
        if (metaInfo) args.add("-meta-info")

        args += listOf("-module-kind", moduleKind)
    }
}
