/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * A service provider for N88 disk basic File.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 */
public class N88DiskBasicFileSpi implements ArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!isSupported(target)) {
            return false;
        }

        // TODO spi accepts only file, implement determining d88 by stream
        String name = ((File) target).getName();
        String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase();

        return extension.equals("d88");
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new N88DiskBasicFile((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"d88", "D88"};
    }
}

/* */
