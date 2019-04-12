/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import com.intellij.util.ArrayUtil
import org.jetbrains.kotlin.backend.common.CodegenUtil
import org.jetbrains.kotlin.codegen.context.MultifileClassPartContext
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.load.java.JvmAnnotationNames
import org.jetbrains.kotlin.load.java.JvmAnnotationNames.METADATA_PACKAGE_NAME_FIELD_NAME
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import org.jetbrains.kotlin.resolve.jvm.diagnostics.OtherOrigin
import org.jetbrains.org.objectweb.asm.Opcodes

class MultifileClassPartCodegen(
    v: ClassBuilder,
    file: KtFile,
    private val packageFragment: PackageFragmentDescriptor,
    private val superClassInternalName: String,
    private val shouldGeneratePartHierarchy: Boolean,
    partContext: MultifileClassPartContext,
    state: GenerationState
) : MemberCodegen<KtFile>(state, null, partContext, file, v) {
    private val partType = partContext.filePartType
    private val facadeClassType = partContext.multifileClassType

    init {
        if (shouldGeneratePartHierarchy && file.declarations.any { it is KtProperty && shouldInitializeProperty(it) }) {
            throw AssertionError("State is not allowed in multi-file classes with -Xmultifile-parts-inherit")
        }
    }

    override fun generate() {
        if (!state.classBuilderMode.generateMultiFileFacadePartClasses) return

        super.generate()

        val generateBodies = state.classBuilderMode.generateBodies

        if (shouldGeneratePartHierarchy) {
            v.newMethod(OtherOrigin(packageFragment), Opcodes.ACC_PUBLIC, "<init>", "()V", null, null).apply {
                if (generateBodies) {
                    visitCode()
                    visitVarInsn(Opcodes.ALOAD, 0)
                    visitMethodInsn(Opcodes.INVOKESPECIAL, superClassInternalName, "<init>", "()V", false)
                    visitInsn(Opcodes.RETURN)
                    visitMaxs(1, 1)
                }
                visitEnd()
            }
        }
    }

    override fun generateDeclaration() {
        val access = if (shouldGeneratePartHierarchy) 0 else Opcodes.ACC_SYNTHETIC or Opcodes.ACC_FINAL

        v.defineClass(
            element, state.classFileVersion, access or Opcodes.ACC_SUPER, partType.internalName, null, superClassInternalName,
            ArrayUtil.EMPTY_STRING_ARRAY
        )
        v.visitSource(element.name, null)

        generatePropertyMetadataArrayFieldIfNeeded(partType)
    }

    override fun generateBody() {
        for (declaration in CodegenUtil.getMemberDeclarationsToGenerate(element)) {
            genSimpleMember(declaration)
        }

        if (state.classBuilderMode.generateBodies) {
            generateInitializers { createOrGetClInitCodegen() }
        }
    }

    override fun generateKotlinMetadataAnnotation() {
        val (serializer, packageProto) = PackagePartCodegen.serializePackagePartMembers(this, partType)

        val extraFlags = if (shouldGeneratePartHierarchy) JvmAnnotationNames.METADATA_MULTIFILE_PARTS_INHERIT_FLAG else 0

        writeKotlinMetadata(v, state, KotlinClassHeader.Kind.MULTIFILE_CLASS_PART, extraFlags) { av ->
            AsmUtil.writeAnnotationData(av, serializer, packageProto)
            av.visit(JvmAnnotationNames.METADATA_MULTIFILE_CLASS_NAME_FIELD_NAME, facadeClassType.internalName)

            val kotlinPackageFqName = element.packageFqName
            if (kotlinPackageFqName != JvmClassName.byInternalName(partType.internalName).packageFqName) {
                av.visit(METADATA_PACKAGE_NAME_FIELD_NAME, kotlinPackageFqName.asString())
            }
        }
    }

    override fun generateSyntheticPartsAfterBody() {
        generateSyntheticAccessors()
    }
}
