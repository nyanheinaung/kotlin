/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example

import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.MANDATORY_WARNING

@AutoService(Processor::class)
class Processor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes() = setOf(SomeAnnotation::class.java.canonicalName)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        processingEnv.messager.printMessage(MANDATORY_WARNING, "*** AP RUNNING ***' ")
        return true
    }
}
