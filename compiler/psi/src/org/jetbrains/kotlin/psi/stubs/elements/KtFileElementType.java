/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements;

import com.intellij.lang.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.parsing.KotlinParser;
import org.jetbrains.kotlin.psi.stubs.KotlinFileStub;
import org.jetbrains.kotlin.psi.stubs.KotlinStubVersions;

import java.io.IOException;

public class KtFileElementType extends IStubFileElementType<KotlinFileStub> {
    private static final String NAME = "kotlin.FILE";

    public KtFileElementType() {
        super(NAME, KotlinLanguage.INSTANCE);
    }

    protected KtFileElementType(@NonNls String debugName) {
        super(debugName, KotlinLanguage.INSTANCE);
    }

    @Override
    public StubBuilder getBuilder() {
        return new KtFileStubBuilder();
    }

    @Override
    public int getStubVersion() {
        return KotlinStubVersions.SOURCE_STUB_VERSION;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return NAME;
    }

    @Override
    public void serialize(@NotNull KotlinFileStub stub, @NotNull StubOutputStream dataStream)
            throws IOException {
        StubIndexService.getInstance().serializeFileStub(stub, dataStream);
    }

    @NotNull
    @Override
    public KotlinFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return StubIndexService.getInstance().deserializeFileStub(dataStream);
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        Project project = psi.getProject();
        Language languageForParser = getLanguageForParser(psi);
        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, languageForParser, chameleon.getChars());
        KotlinParser parser = (KotlinParser) LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser).createParser(project);
        return parser.parse(this, builder, psi.getContainingFile()).getFirstChildNode();
    }

    @Override
    public void indexStub(@NotNull KotlinFileStub stub, @NotNull IndexSink sink) {
        StubIndexService.getInstance().indexFile(stub, sink);
    }
}
