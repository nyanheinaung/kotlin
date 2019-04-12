/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.org.objectweb.asm.tree.FieldInsnNode

interface TransformationInfo {
    val oldClassName: String

    val newClassName: String
        get() = nameGenerator.generatorClass

    val nameGenerator: NameGenerator

    val wasAlreadyRegenerated: Boolean
        get() = false


    fun shouldRegenerate(sameModule: Boolean): Boolean

    fun canRemoveAfterTransformation(): Boolean

    fun createTransformer(inliningContext: InliningContext, sameModule: Boolean, continuationClassName: String?): ObjectTransformer<*>
}

class WhenMappingTransformationInfo(
    override val oldClassName: String,
    parentNameGenerator: NameGenerator,
    private val alreadyRegenerated: Boolean,
    val fieldNode: FieldInsnNode
) : TransformationInfo {

    override val nameGenerator by lazy {
        parentNameGenerator.subGenerator(false, oldClassName.substringAfterLast("/").substringAfterLast(TRANSFORMED_WHEN_MAPPING_MARKER))
    }

    override fun shouldRegenerate(sameModule: Boolean): Boolean = !alreadyRegenerated && !sameModule

    override fun canRemoveAfterTransformation(): Boolean = true

    override fun createTransformer(
        inliningContext: InliningContext,
        sameModule: Boolean,
        continuationClassName: String?
    ): ObjectTransformer<*> =
        WhenMappingTransformer(this, inliningContext)

    companion object {
        const val TRANSFORMED_WHEN_MAPPING_MARKER = "\$wm$"
    }
}

class AnonymousObjectTransformationInfo internal constructor(
    override val oldClassName: String,
    private val needReification: Boolean,
    val functionalArguments: Map<Int, FunctionalArgument>,
    private val capturedOuterRegenerated: Boolean,
    private val alreadyRegenerated: Boolean,
    val constructorDesc: String?,
    private val isStaticOrigin: Boolean,
    parentNameGenerator: NameGenerator,
    private val capturesAnonymousObjectThatMustBeRegenerated: Boolean = false
) : TransformationInfo {

    override val nameGenerator by lazy {
        parentNameGenerator.subGenerator(true, null)
    }

    lateinit var newConstructorDescriptor: String

    lateinit var allRecapturedParameters: List<CapturedParamDesc>

    lateinit var capturedLambdasToInline: Map<String, LambdaInfo>

    override val wasAlreadyRegenerated: Boolean
        get() = alreadyRegenerated

    constructor(
        ownerInternalName: String,
        needReification: Boolean,
        alreadyRegenerated: Boolean,
        isStaticOrigin: Boolean,
        nameGenerator: NameGenerator
    ) : this(ownerInternalName, needReification, hashMapOf(), false, alreadyRegenerated, null, isStaticOrigin, nameGenerator)

    override fun shouldRegenerate(sameModule: Boolean): Boolean =
        !alreadyRegenerated &&
                (!functionalArguments.isEmpty() || !sameModule || capturedOuterRegenerated || needReification || capturesAnonymousObjectThatMustBeRegenerated)

    override fun canRemoveAfterTransformation(): Boolean {
        // Note: It is unsafe to remove anonymous class that is referenced by GETSTATIC within lambda
        // because it can be local function from outer scope
        return !isStaticOrigin
    }

    override fun createTransformer(
        inliningContext: InliningContext,
        sameModule: Boolean,
        continuationClassName: String?
    ): ObjectTransformer<*> =
        AnonymousObjectTransformer(this, inliningContext, sameModule, continuationClassName)
}
