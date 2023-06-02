/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.spi.ArchiveSpi;


/**
 * A service provider base for ASAR.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/29 umjammer initial version <br>
 */
public abstract class AsarArchiveSpi implements ArchiveSpi {

    /**
     *
     * @param is need to support mark
     */
    protected boolean canExtractInput(InputStream is, boolean needToClose) throws IOException {

        byte[] b = new byte[4];

        is.mark(4);
        int l = 0;
        while (l < 4) {
            l += is.read(b, l, 4 - l);
        }
        is.reset();

        if (needToClose) {
            is.close();
        }

        return b[0] == 0x04 &&
               b[1] == 0x00 &&
               b[2] == 0x00 &&
               b[3] == 0x00;
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"asar"};
    }
}

/* */
