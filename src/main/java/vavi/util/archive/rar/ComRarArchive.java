/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.MessageFormat;

import vavi.util.archive.ComArchive;
import vavi.util.archive.Entry;

import static java.lang.System.getLogger;


/**
 * A service provider for RAR archive file using windows COM.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030211 nsano initial version <br>
 */
public class ComRarArchive extends ComArchive {

    private static final Logger logger = getLogger(ComRarArchive.class.getName());

    /** */
    public ComRarArchive(File file) throws IOException {
        super(file, TYPE_RAR);
    }

    /** */
    private static final MessageFormat commandLineBase = new MessageFormat("x -o -q \"{0}\" \"{1}\" \"{2}\"");

    @Override
    protected String getCommandString(Entry entry) {

        String commandLine = commandLineBase.format(new Object[] {
            file.getPath(),
            System.getProperty("java.io.tmpdir"),
            entry.getName()
        });
logger.log(Level.DEBUG, "commandLine: " + commandLine);

        return commandLine;
    }

    @Override
    protected String getTemporaryFileName(Entry entry) {
        return System.getProperty("java.io.tmpdir") + entry.getName();
    }
}
