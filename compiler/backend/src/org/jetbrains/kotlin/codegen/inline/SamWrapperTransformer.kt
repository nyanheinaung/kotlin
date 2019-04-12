/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.org.objectweb.asm.ClassReader
import org.jetbrains.org.objectweb.asm.ClassVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

class SamWrapperTransformationInfo(
    override val oldClassName: String,
    private val inliningContext: InliningContext,
    private val alreadyRegenerated: Boolean
) : TransformationInfo {
    override val nameGenerator: NameGenerator
        get() = object : NameGenerator("stub") {
            override fun getGeneratorClass(): String {
                error("Shouldn't be called on $oldClassName transformation")
            }

            override fun subGenerator(inliningMethod: String?): NameGenerator {
                error("Shouldn't be called on $oldClassName transformation")
            }

            override fun subGenerator(lambdaNoWhen: Boolean, nameSuffix: String?): NameGenerator {
                error("Shouldn't be called on $oldClassName transformation")
            }
        }

    //TODO consider to use package class instead of inliningContext.root.callSiteInfo.ownerClassName
    override val newClassName: String
        get() = inliningContext.root.callSiteInfo.ownerClassName + "\$inlined" + "\$sam$".run { this + oldClassName.substringAfter(this) }

    override fun shouldRegenerate(sameModule: Boolean) = !sameModule && !alreadyRegenerated

    override fun canRemoveAfterTransformation() = false

    override fun createTransformer(inliningContext: InliningContext, sameModule: Boolean, continuationClassName: String?) =
        SamWrapperTransformer(this, inliningContext)
}

class SamWrapperTransformer(transformationInfo: SamWrapperTransformationInfo, private val inliningContext: InliningContext) :
    ObjectTransformer<SamWrapperTransformationInfo>(transformationInfo, inliningContext.state) {

    override fun doTransform(parentRemapper: FieldRemapper): InlineResult {
        val classReader = createClassReader()
        val classBuilder = createRemappingClassBuilderViaFactory(inliningContext)

        classReader.accept(object : ClassVisitor(Opcodes.API_VERSION, classBuilder.visitor) {
            override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String, interfaces: Array<String>) {
                classBuilder.defineClass(null, version, access, name, signature, superName, interfaces)
            }

        }, ClassReader.SKIP_FRAMES)
        classBuilder.done()

        return transformationResult
    }
}