/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.MessageFormat;

import vavi.util.archive.ComArchive;
import vavi.util.archive.Entry;

import static java.lang.System.getLogger;


/**
 * A service provider for CAB archive file using windows COM.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class ComCabArchive extends ComArchive {

    private static final Logger logger = getLogger(ComCabArchive.class.getName());

    /** */
    public ComCabArchive(File file) throws IOException {
        super(file, TYPE_CAB);
    }

    /** */
    private static final MessageFormat commandLineBase = new MessageFormat("-x -i -o \"{0}\" \"{1}\" \"{2}\"");

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
