/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public final class ResourceData {
    public final File jarFile;
    public final String resourceName;
    public final byte[] bytes;

    public ResourceData(File jarFile, String resourceName, byte[] bytes) {
        this.jarFile = jarFile;
        this.resourceName = resourceName;
        this.bytes = bytes;
    }

    public URL getURL() {
        try {
            String path = "file:" + jarFile + "!/" + resourceName;
            return new URL("jar", null, 0, path, new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(u) {
                        @Override
                        public void connect() throws IOException {}

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new ByteArrayInputStream(bytes);
                        }
                    };
                }
            });
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
