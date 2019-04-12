/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid

interface FileLoweringPass {
    fun lower(irFile: IrFile)
}

interface ClassLoweringPass : FileLoweringPass {
    fun lower(irClass: IrClass)

    override fun lower(irFile: IrFile) = runOnFilePostfix(irFile)
}

interface DeclarationContainerLoweringPass : FileLoweringPass {
    fun lower(irDeclarationContainer: IrDeclarationContainer)

    override fun lower(irFile: IrFile) = runOnFilePostfix(irFile)
}

interface FunctionLoweringPass : FileLoweringPass {
    fun lower(irFunction: IrFunction)

    override fun lower(irFile: IrFile) = runOnFilePostfix(irFile)
}

interface BodyLoweringPass : FileLoweringPass {
    fun lower(irBody: IrBody)

    override fun lower(irFile: IrFile) = runOnFilePostfix(irFile)
}

fun FileLoweringPass.lower(moduleFragment: IrModuleFragment) = moduleFragment.files.forEach { lower(it) }

fun ClassLoweringPass.runOnFilePostfix(irFile: IrFile) {
    irFile.acceptVoid(object : IrElementVisitorVoid {
        override fun visitElement(element: IrElement) {
            element.acceptChildrenVoid(this)
        }

        override fun visitClass(declaration: IrClass) {
            declaration.acceptChildrenVoid(this)
            lower(declaration)
        }
    })
}

fun DeclarationContainerLoweringPass.asClassLoweringPass() = object : ClassLoweringPass {
    override fun lower(irClass: IrClass) {
        this@asClassLoweringPass.lower(irClass)
    }
}

fun DeclarationContainerLoweringPass.runOnFilePostfix(irFile: IrFile) {
    this.asClassLoweringPass().runOnFilePostfix(irFile)
    this.lower(irFile as IrDeclarationContainer)
}

fun BodyLoweringPass.runOnFilePostfix(irFile: IrFile) {
    irFile.acceptVoid(object : IrElementVisitorVoid {
        override fun visitElement(element: IrElement) {
            element.acceptChildrenVoid(this)
        }

        override fun visitBody(body: IrBody) {
            body.acceptChildrenVoid(this)
            lower(body)
        }
    })
}

fun FunctionLoweringPass.runOnFilePostfix(irFile: IrFile) {
    irFile.acceptVoid(object : IrElementVisitorVoid {
        override fun visitElement(element: IrElement) {
            element.acceptChildrenVoid(this)
        }

        override fun visitFunction(declaration: IrFunction) {
            declaration.acceptChildrenVoid(this)
            lower(declaration)
        }
    })
}