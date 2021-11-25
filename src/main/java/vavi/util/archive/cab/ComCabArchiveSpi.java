/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.Archive;


/**
 * The SPI for CAB archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class ComCabArchiveSpi extends CabArchiveSpi {

    /**
     * @param target currently accepts {@link File}, {@link InputStream} only.
     */
    public boolean canExtractInput(Object target) throws IOException {
        InputStream is;
        boolean needToClose = false;

        if (File.class.isInstance(target)) {
            is = new BufferedInputStream(new FileInputStream(File.class.cast(target)));
            needToClose = true;
        } else {
            throw new IllegalArgumentException("not supported type " + target.getClass().getName());
        }

        return canExtractInput(is, needToClose);
    }

    /* */
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new ComCabArchive((File) obj);
    }
}

/* */
