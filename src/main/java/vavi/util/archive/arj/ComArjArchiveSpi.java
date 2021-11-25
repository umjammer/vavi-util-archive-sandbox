/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.File;
import java.io.IOException;

import vavi.util.archive.Archive;


/**
 * The SPI for ARJ archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class ComArjArchiveSpi extends ArjArchiveSpi {

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new ComArjArchive((File) obj);
    }
}

/* */
