/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The SPI for ASAR archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/14 umjammer initial version <br>
 */
public class AsarArchiveSpi implements ArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        InputStream is =
            new BufferedInputStream(Files.newInputStream(((File) target).toPath()));

        byte[] b = new byte[2];

        is.mark(2);
        int l = 0;
        while (l < 2) {
            l += is.read(b, l, 2 - l);
        }
        is.reset();

        is.close();

        return b[0] == 'P' &&
               b[1] == 'K';
    }

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new AsarArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"asar"};
    }
}

/* */
