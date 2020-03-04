/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.File;
import java.io.IOException;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * N88 Disk Basic File を処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 */
public class N88DiskBasicFileSpi implements ArchiveSpi {

    /**
     * 解凍できるかどうか調べます．
     */
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        String name = ((File) target).getName();
        String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase();

        return extension.equals("d88");
    }

    /** */
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new N88DiskBasicFile((File) obj);
    }
}

/* */
