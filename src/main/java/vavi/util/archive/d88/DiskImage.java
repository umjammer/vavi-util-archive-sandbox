/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.IOException;
import java.io.InputStream;


/**
 * Represents a common disk image.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 010819 nsano initial version <br>
 */
public interface DiskImage {

    /** */
    byte[] readData(int track, int surface, int sector);

    enum Density {
        _2D,
        _2DD,
        _2HD,
        UNKNOWN
    }

    Density getDensity();

    /** */
    class Factory {
        /**
         * TODO read header and dispatch image type.
         */
        public static DiskImage readFrom(InputStream is) throws IOException {
            return D88.readFrom(is);
        }
    }
}

/* */
