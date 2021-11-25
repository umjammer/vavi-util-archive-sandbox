/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import vavi.util.Debug;
import vavi.util.archive.ComArchive;
import vavi.util.archive.Entry;


/**
 * RAR アーカイブを処理するサービスプロバイダです．
 * (COM バージョン)
 *
 * @target 1.1
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030211 nsano initial version <br>
 */
public class ComRarArchive extends ComArchive {

    /** */
    public ComRarArchive(File file) throws IOException {
        super(file, TYPE_RAR);
    }

    /** */
    private static final MessageFormat commandLineBase = new MessageFormat("x -o -q \"{0}\" \"{1}\" \"{2}\"");

    /** */
    protected String getCommandString(Entry entry) {

        String commandLine = commandLineBase.format(new Object[] {
            file.getPath(),
            System.getProperty("java.io.tmpdir"),
            entry.getName()
        });
Debug.println("commandLine: " + commandLine);

        return commandLine;
    }

    /** */
    protected String getTemporaryFileName(Entry entry) {
        return System.getProperty("java.io.tmpdir") + entry.getName();
    }
}

/* */
