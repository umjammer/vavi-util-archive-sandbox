/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.anatawa12.asar4j.AsarEntry;
import com.anatawa12.asar4j.AsarFile;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * Represents ASAR archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/24 umjammer initial version <br>
 */
public class Asar4jArchive implements Archive {

    /** */
    private AsarFile archive;

    private String name;

    private long size;

    /** */
    public Asar4jArchive(File file) throws IOException {
        this.archive = new AsarFile(file);
        this.name = file.getName();
        this.size = file.length();
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }

    @Override
    public Entry[] entries() {
        List<Entry> entries = new ArrayList<>();
        for (AsarEntry e : archive.iterable()) {
            entries.add(new Asar4jEntry(e));
        }
        return entries.toArray(new Entry[0]);
    }

    @Override
    public Entry getEntry(String name) {
        for (AsarEntry e : archive.iterable()) {
            if (name.equals(e.getName())) {
                return new Asar4jEntry(e);
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        for (AsarEntry e : archive.iterable()) {
            if (entry.getName().equals(e.getName())) {
                return archive.getInputStream(e);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return (int) size;
    }
}

/* */
