/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import vavi.util.archive.Archive;
import vavi.util.archive.zip.JdkZipArchive;


/**
 * The SPI for ARJ archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/17 umjammer initial version <br>
 */
public class ApacheArjArchiveSpi extends ArjArchiveSpi {

    /**
     * @param target currently accepts {@link File} only.
     */
    @Override
    public boolean canExtractInput(Object target) throws IOException {
        if (!isSupported(target)) {
            return false;
        }

        InputStream is = null;
        boolean needToClose = false;

        if (target instanceof File) {
            is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));
            needToClose = true;
        } else if (target instanceof InputStream) {
            is = (InputStream) target;
            if (!is.markSupported()) {
                throw new IllegalArgumentException("InputStream should support #mark()");
            }
        } else {
            assert false : target.getClass().getName();
        }

        return super.canExtractInput(is, needToClose);
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        if (obj instanceof File) {
            return new ApacheArjArchive((File) obj);
        } else if (obj instanceof InputStream) {
            return new ApacheArjArchive((InputStream) obj);
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class, InputStream.class};
    }
}

/* */
