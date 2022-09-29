/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.xar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sprylab.xar.FileXarSource;
import com.sprylab.xar.XarException;
import com.sprylab.xar.XarSource;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;
import vavi.util.archive.WrappedEntry;


/**
 * A service provider which is processing XAR archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/10/07 umjammer initial version <br>
 */
public class XarArchive extends InputStreamSupport implements Archive {

    /** */
    private XarSource archive;

    /** */
    private String name;

    /** */
    private Entry[] entries;

    /** */
    public XarArchive(File file) {
        this.archive = new FileXarSource(file);
        this.name = file.getName();
    }

    /** */
    public XarArchive(InputStream is) throws IOException {
        super(is);
        this.archive = new FileXarSource(archiveFileForInputStream);
        this.name = archiveFileForInputStream.getPath();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Entry[] entries() {
        if (entries == null) {
            try {
                List<Entry> entries = new ArrayList<>();
                for (com.sprylab.xar.XarEntry e : archive.getEntries()) {
                    entries.add(new XarEntry(e));
                }
                this.entries = entries.toArray(new Entry[0]);
            } catch (XarException e) {
                throw new IllegalStateException(e);
            }
        }
        return this.entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries()) {
//Debug.println("@@@: " + name + ", " + e.getName());
            if (name.equals(entry.getName())) {
                return entry;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public InputStream getInputStream(Entry entry) throws IOException {
        for (Entry e : entries()) {
            if (entry.getName().equals(e.getName())) {
                return new ByteArrayInputStream(((WrappedEntry<com.sprylab.xar.XarEntry>) e).getWrappedObject().getBytes());
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
        return entries().length;
    }
}

/* */
