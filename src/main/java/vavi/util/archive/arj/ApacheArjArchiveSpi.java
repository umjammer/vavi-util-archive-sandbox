/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
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
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/17 umjammer initial version <br>
 */
public class ApacheArjArchiveSpi extends ArjArchiveSpi {

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new ApacheArjArchive((File) obj);
    }
}

/* */
