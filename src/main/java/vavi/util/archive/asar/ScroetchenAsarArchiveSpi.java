/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import vavi.util.archive.Archive;


/**
 * The SPI for ASAR archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/14 umjammer initial version <br>
 */
public class ScroetchenAsarArchiveSpi extends AsarArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!isSupported(target)) {
            return false;
        }

        InputStream is =
            new BufferedInputStream(Files.newInputStream(((File) target).toPath()));

        return super.canExtractInput(is, false);
    }

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new ScroetchenAsarArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }
}

/* */
