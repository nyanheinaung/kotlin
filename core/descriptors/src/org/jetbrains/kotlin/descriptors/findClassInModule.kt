/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.ClassId

fun ModuleDescriptor.findClassAcrossModuleDependencies(classId: ClassId): ClassDescriptor? {
    val packageViewDescriptor = getPackage(classId.packageFqName)
    val segments = classId.relativeClassName.pathSegments()
    val topLevelClass = packageViewDescriptor.memberScope.getContributedClassifier(segments.first(), NoLookupLocation.FROM_DESERIALIZATION) as? ClassDescriptor ?: return null
    var result = topLevelClass
    for (name in segments.subList(1, segments.size)) {
        result = result.unsubstitutedInnerClassesScope.getContributedClassifier(name, NoLookupLocation.FROM_DESERIALIZATION) as? ClassDescriptor ?: return null
    }
    return result
}

// Returns a mock class descriptor if no existing class is found.
// NB: the returned class has no type parameters and thus cannot be given arguments in types
fun ModuleDescriptor.findNonGenericClassAcrossDependencies(classId: ClassId, notFoundClasses: NotFoundClasses): ClassDescriptor {
    val existingClass = findClassAcrossModuleDependencies(classId)
    if (existingClass != null) return existingClass

    // Take a list of N zeros, where N is the number of class names in the given ClassId
    val typeParametersCount = generateSequence(classId, ClassId::getOuterClassId).map { 0 }.toList()

    return notFoundClasses.getClass(classId, typeParametersCount)
}

fun ModuleDescriptor.findTypeAliasAcrossModuleDependencies(classId: ClassId): TypeAliasDescriptor? {
    // TODO refactor with findClassAcrossModuleDependencies
    // TODO what if typealias becomes a class / interface?

    val packageViewDescriptor = getPackage(classId.packageFqName)
    val segments = classId.relativeClassName.pathSegments()
    val lastNameIndex = segments.size - 1
    val topLevelClassifier = packageViewDescriptor.memberScope.getContributedClassifier(segments.first(), NoLookupLocation.FROM_DESERIALIZATION)
    if (lastNameIndex == 0) return topLevelClassifier as? TypeAliasDescriptor

    var currentClass = topLevelClassifier as? ClassDescriptor ?: return null
    for (name in segments.subList(1, lastNameIndex)) {
        currentClass = currentClass.unsubstitutedInnerClassesScope.getContributedClassifier(name, NoLookupLocation.FROM_DESERIALIZATION) as? ClassDescriptor ?: return null
    }
    val lastName = segments[lastNameIndex]
    return currentClass.unsubstitutedMemberScope.getContributedClassifier(lastName, NoLookupLocation.FROM_DESERIALIZATION) as? TypeAliasDescriptor
}
