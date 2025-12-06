/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;
import vavi.util.cab.Cab;
import vavi.util.cab.CabFile;
import vavi.util.cab.CabFolder;

import static java.lang.System.getLogger;


/**
 * Represents a CAB archive file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
public class PureJavaCabArchive implements Archive {

    private static final Logger logger = getLogger(PureJavaCabArchive.class.getName());

    /** */
    private Cab cab;
    /** */
    private InputStream is;
    /** */
    private final int size;
    /** */
    private final String name;

    /** */
    private final List<Entry> entries = new ArrayList<>();

    /** */
    public PureJavaCabArchive(File file) throws IOException {
        this.size = (int) file.length();
        this.name = file.getName();
        init(Files.newInputStream(file.toPath()));
    }

    /** */
    public PureJavaCabArchive(InputStream is) throws IOException {
        this.size = is.available();
        this.name = is.toString();
        init(is);
    }

    /** */
    private void init(InputStream is) throws IOException {
        this.is = is;
        this.cab = new Cab(is, 1);

logger.log(Level.DEBUG, cab.getFolders().size());
        for (CabFolder folder : cab.getFolders()) {
            for (CabFile cabFile : folder.getFiles()) {
                CommonEntry entry = new CommonEntry();
                entry.setName(folder + File.separator + cabFile.getFileName());
                // TODO entry.set...
                entries.add(entry);
            }
        }
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public Entry[] entries() {
        return entries.toArray(Entry[]::new);
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
          if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /** reads a CAB file, parses it, and returns an InputStream representing the named file */
    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return size;
    }
}
