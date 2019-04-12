/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.sam;

import org.jetbrains.kotlin.descriptors.FunctionDescriptor;
import org.jetbrains.kotlin.descriptors.synthetic.SyntheticMemberDescriptor;
import org.jetbrains.kotlin.load.java.descriptors.JavaCallableMemberDescriptor;

public interface SamAdapterDescriptor<D extends FunctionDescriptor> extends FunctionDescriptor, JavaCallableMemberDescriptor,
                                                                            SyntheticMemberDescriptor<D> {
}
