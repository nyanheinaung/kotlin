/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub;
import org.jetbrains.kotlin.psi.stubs.impl.KotlinImportDirectiveStubImpl;

import java.io.IOException;

public class KtImportDirectiveElementType extends KtStubElementType<KotlinImportDirectiveStub, KtImportDirective> {
    public KtImportDirectiveElementType(@NotNull @NonNls String debugName) {
        super(debugName, KtImportDirective.class, KotlinImportDirectiveStub.class);
    }

    @NotNull
    @Override
    public KotlinImportDirectiveStub createStub(@NotNull KtImportDirective psi, StubElement parentStub) {
        FqName importedFqName = psi.getImportedFqName();
        StringRef fqName = StringRef.fromString(importedFqName == null ? null : importedFqName.asString());
        return new KotlinImportDirectiveStubImpl((StubElement<?>) parentStub, psi.isAllUnder(), fqName, psi.isValidImport());
    }

    @Override
    public void serialize(@NotNull KotlinImportDirectiveStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(stub.isAllUnder());
        FqName importedFqName = stub.getImportedFqName();
        dataStream.writeName(importedFqName != null ? importedFqName.asString() : null);
        dataStream.writeBoolean(stub.isValid());
    }

    @NotNull
    @Override
    public KotlinImportDirectiveStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        boolean isAllUnder = dataStream.readBoolean();
        StringRef importedName = dataStream.readName();
        boolean isValid = dataStream.readBoolean();
        return new KotlinImportDirectiveStubImpl((StubElement<?>) parentStub, isAllUnder, importedName, isValid);
    }
}
