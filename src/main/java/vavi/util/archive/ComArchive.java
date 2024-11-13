/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import vavix.util.ComUtil;

import static java.lang.System.getLogger;


/**
 * Represents a KBA front end．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030211 nsano initial version <br>
 * @see "https://www.madobe.net/archiver/lib/activex/kba.html"
 * @see "https://github.com/freemansoft/jacob-project"
 */
public abstract class ComArchive implements Archive {

    private static final Logger logger = getLogger(ComArchive.class.getName());

    /** */
    public static final String TYPE_LHA = "UNLHA";
    /** */
    public static final String TYPE_FTP = "FTP";
    /** */
    public static final String TYPE_TAR = "TAR";
    /** */
    public static final String TYPE_CAB = "CAB";
    /** */
    public static final String TYPE_ZIP = "ZIP";
    /** */
    public static final String TYPE_ARJ = "UNARJ";
    /** */
    public static final String TYPE_RAR = "UNRAR";
    /** */
    public static final String TYPE_UNZIP = "UNZIP";
    /** */
    public static final String TYPE_ISH = "ISH";

    /** */
    private final String type;

    /** */
    private final List<Entry> entries = new ArrayList<>();

    /** KBA manager */
    private final Dispatch manager;
    /** archive COM */
    private final Dispatch module;

    /** */
    protected File file;

    /**
     * @throws IOException file not found
     */
    public ComArchive(File file, String type) throws IOException {

        this.file = file;
        this.type = type;
logger.log(Level.DEBUG, "type: " + this.type);
        ComThread.InitSTA();

        // manager
        ActiveXComponent activex = new ActiveXComponent("KBA.Manager");
        manager = activex.getObject();

logger.log(Level.DEBUG, "version: " + Dispatch.get(manager, "Ver"));
logger.log(Level.DEBUG, "unlha: " + Dispatch.get(manager, "UnlhaOk").getBoolean());
logger.log(Level.DEBUG, "ftp: "   + Dispatch.get(manager, "FtpOk").getBoolean());
logger.log(Level.DEBUG, "cab: "   + Dispatch.get(manager, "CabOk").getBoolean());
logger.log(Level.DEBUG, "zip: "   + Dispatch.get(manager, "ZipOk").getBoolean());
logger.log(Level.DEBUG, "unarj: " + Dispatch.get(manager, "UnarjOk").getBoolean());
logger.log(Level.DEBUG, "unrar: " + Dispatch.get(manager, "UnrarOk").getBoolean());
logger.log(Level.DEBUG, "unzip: " + Dispatch.get(manager, "UnzipOk").getBoolean());
logger.log(Level.DEBUG, "ish: "   + Dispatch.get(manager, "IshOk").getBoolean());

        Variant result = Dispatch.invoke(manager, "ArcClass", Dispatch.Method, new Object[] { type }, new int[1]);
logger.log(Level.DEBUG, "arcClass: " + ComUtil.toObject(result));

        // each module
        activex = new ActiveXComponent("KBA." + type);
logger.log(Level.DEBUG, "activex: " + "KBA." + type);
        module = activex.getObject();

if ("ZIP".equals(type)) {
 logger.log(Level.DEBUG, "version: " + Dispatch.get(module, "VerUnZip"));
} else {
 logger.log(Level.DEBUG, "version: " + Dispatch.get(module, "Ver"));
}
logger.log(Level.DEBUG, "interval: " + Dispatch.get(module, "CursorInterval"));
logger.log(Level.DEBUG, "background: " + Dispatch.get(module, "BackgroundMode"));
logger.log(Level.DEBUG, "cursorMode: " + Dispatch.get(module, "CursorMode"));
logger.log(Level.DEBUG, "running: " + Dispatch.get(module, "Running"));
        result = Dispatch.invoke(module, "OpenArc", Dispatch.Method, new Object[] { file.toString() }, new int[1]);
logger.log(Level.DEBUG, "openArc: " + ComUtil.toObject(result));
        if (result.getInt() == 0) {
            throw new FileNotFoundException(file.toString());
        }

        result = Dispatch.invoke(module, "Find", Dispatch.Method, new Object[] { "*" }, new int[1]);
        if (!result.getBoolean()) {
logger.log(Level.DEBUG, "no content");
            return;
        }

        do {
            CommonEntry entry = new CommonEntry();

            Variant value = Dispatch.get(module, "FileName");
logger.log(Level.DEBUG, "name: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setName(value.getString());

            value = Dispatch.get(module, "FileTime");
logger.log(Level.DEBUG, "time: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setTime(value.getJavaDate().getTime());

            value = Dispatch.get(module, "FileAttr");
logger.log(Level.DEBUG, "attr: " + value.getClass() + ": " + ComUtil.toObject(value));

            value = Dispatch.get(module, "FileMode");
logger.log(Level.DEBUG, "mode: " + value.getClass() + ": " + ComUtil.toObject(value));

            value = Dispatch.get(module, "OriginalSize");
logger.log(Level.DEBUG, "size: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setSize(value.getInt());

            value = Dispatch.get(module, "CompressedSize");
logger.log(Level.DEBUG, "compressed: " + value.getClass() + ": " + ComUtil.toObject(value));
            entry.setCompressedSize(value.getInt());

            value = Dispatch.get(module, "Ratio");
logger.log(Level.DEBUG, "ratio: " + value.getClass() + ": " + ComUtil.toObject(value));

            entries.add(entry);
//logger.log(Level.DEBUG, StringUtil.paramString(entry));

            result = Dispatch.invoke(module, "FindNext", Dispatch.Method, new Object[] {}, new int[1]);
logger.log(Level.DEBUG, "findNext: " + ComUtil.toObject(result));
        } while (!result.getBoolean());
/*
        "ArcDateTime";
        "ArcOriginalSize";
        "ArcCompressedSize";
        "ArcRatio";
*/
    }

    @Override
    public void close() throws IOException {
        Variant result = Dispatch.invoke(module, "CloseArc", Dispatch.Method, new Object[] {}, new int[1]);
logger.log(Level.DEBUG, ComUtil.toObject(result));
        ComThread.Release();
    }

    @Override
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
//logger.log(Level.DEBUG, entry.getName() + ", " + name);
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        Variant result = Dispatch.invoke(module, "ArcCmd", Dispatch.Method, new Object[] { getCommandString(entry) }, new int[1]);
logger.log(Level.DEBUG, "ArcCmd: " + ComUtil.toObject(result));
        String resultString = Dispatch.get(module, "ArcCmdRes").getString();
logger.log(Level.DEBUG, "result: " + resultString);
        if (result.getInt() != 0) {
            throw new IOException(resultString);
        }

        String temporaryFileName = getTemporaryFileName(entry);
        File temporaryFile = new File(temporaryFileName);
        if (temporaryFile.exists()) {
            return new BufferedInputStream(Files.newInputStream(temporaryFile.toPath()));
        } else {
            throw new IOException("cannot extract: " + temporaryFileName);
        }
    }

    /** Returns the command line to pass to COM. */
    protected abstract String getCommandString(Entry entry);

    /** Returns the unzipped filename. */
    protected abstract String getTemporaryFileName(Entry entry);

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public int size() {
        return entries.size();
    }
}
