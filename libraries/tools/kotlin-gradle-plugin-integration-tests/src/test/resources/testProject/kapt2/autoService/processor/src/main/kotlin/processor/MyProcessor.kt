/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package processor

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes("annotation.ProcessThis")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class MyProcessor() : AbstractProcessor() {

    private var fileCreated = false

    override fun process(annotations: Set<TypeElement>,
            roundEnv: RoundEnvironment): Boolean {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "Working!")
        if (fileCreated) return true
        fileCreated = true
        val file = processingEnv.filer.createSourceFile("Check")
        file.openWriter().use {
            it.appendln("// $annotations")
            it.appendln("public class Check {}")
        }
        return true
    }
}