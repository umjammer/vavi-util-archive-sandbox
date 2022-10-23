/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.spi.ArchiveSpi;


/**
 * The SPI base for ARJ.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 220929 nsano initial version <br>
 */
public abstract class ArjArchiveSpi implements ArchiveSpi {

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

        is.close();

        return b[0] == 'A' && // TODO
               b[1] == 'R' &&
               b[2] == 'J';
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"arj", "ARJ"};
    }
}

/* */
