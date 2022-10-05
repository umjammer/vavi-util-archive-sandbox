/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;
import vavi.util.cab.Cab;
import vavi.util.cab.CabFile;
import vavi.util.cab.CabFolder;


/**
 * PureJavaCabArchive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
public class PureJavaCabArchive implements Archive {

    /** */
    private Cab cab;
    /** */
    private InputStream is;
    /** */
    private int size;
    /** */
    private String name;

    /** */
    private List<Entry> entries = new ArrayList<>();

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

Debug.println(cab.getFolders().size());
        for (CabFolder folder : cab.getFolders()) {
            for (CabFile cabFile : folder.getFiles()) {
                CommonEntry entry = new CommonEntry();
                entry.setName(folder + File.separator + cabFile.getFileName());
                // TODO entry.set...
                entries.add(entry);
            }
        }
    }

    /** */
    public void close() throws IOException {
        is.close();
    }

    /** */
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    /** */
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
          if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /** reads a CAB file, parses it, and returns an InputStream representing the named file */
    public InputStream getInputStream(Entry entry) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /** */
    public String getName() {
        return name;
    }

    /** */
    public int size() {
        return size;
    }
}

/* */
