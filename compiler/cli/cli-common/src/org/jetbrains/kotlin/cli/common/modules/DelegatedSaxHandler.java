/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.modules;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

public abstract class DelegatedSaxHandler extends DefaultHandler {

    @NotNull
    protected abstract DefaultHandler getDelegate();

    @Override
    public InputSource resolveEntity(String publicId, @NotNull String systemId) throws IOException, SAXException {
        return getDelegate().resolveEntity(publicId, systemId);
    }

    @Override
    public void notationDecl(@NotNull String name, String publicId, String systemId) throws SAXException {
        getDelegate().notationDecl(name, publicId, systemId);
    }

    @Override
    public void unparsedEntityDecl(@NotNull String name, String publicId, @NotNull String systemId, String notationName)
            throws SAXException {
        getDelegate().unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public void setDocumentLocator(@NotNull Locator locator) {
        getDelegate().setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        getDelegate().startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        getDelegate().endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, @NotNull String uri) throws SAXException {
        getDelegate().startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        getDelegate().endPrefixMapping(prefix);
    }

    @Override
    public void startElement(@NotNull String uri, @NotNull String localName, @NotNull String qName, @NotNull Attributes attributes)
            throws SAXException {
        getDelegate().startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, @NotNull String localName, @NotNull String qName) throws SAXException {
        getDelegate().endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        getDelegate().characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        getDelegate().ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(@NotNull String target, @NotNull String data) throws SAXException {
        getDelegate().processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(@NotNull String name) throws SAXException {
        getDelegate().skippedEntity(name);
    }

    @Override
    public void warning(@NotNull SAXParseException e) throws SAXException {
        getDelegate().warning(e);
    }

    @Override
    public void error(@NotNull SAXParseException e) throws SAXException {
        getDelegate().error(e);
    }

    @Override
    public void fatalError(@NotNull SAXParseException e) throws SAXException {
        getDelegate().fatalError(e);
    }
}
