/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import vavi.util.archive.Archive;


/**
 * A service provider for ASAR archive file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/24 umjammer initial version <br>
 */
public class Asar4jArchiveSpi extends AsarArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!isSupported(target)) {
            return false;
        }

        InputStream is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));

        return super.canExtractInput(is, false);
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new Asar4jArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }
}

/* */
