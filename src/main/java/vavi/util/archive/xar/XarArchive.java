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


/**
 * A service provider which is processing XAR archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/10/07 umjammer initial version <br>
 */
public class XarArchive implements Archive {

    /** */
    private XarSource archive;

    private String name;

    private long size;

    /** */
    public XarArchive(File file) throws IOException {
        this.archive = new FileXarSource(file);
        this.name = file.getName();
        this.size = file.length();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Entry[] entries() {
        try {
            List<Entry> entries = new ArrayList<>();
            for (com.sprylab.xar.XarEntry e : archive.getEntries()) {
                entries.add(new XarEntry(e));
            }
            return entries.toArray(new Entry[0]);
        } catch (XarException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Entry getEntry(String name) {
        try {
            for (com.sprylab.xar.XarEntry e : archive.getEntries()) {
//Debug.println("@@@: " + name + ", " + e.getName());
                if (name.equals(e.getName())) {
                    return new XarEntry(e);
                }
            }
            return null;
        } catch (XarException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        for (com.sprylab.xar.XarEntry e : archive.getEntries()) {
            if (entry.getName().equals(e.getName())) {
                return new ByteArrayInputStream(e.getBytes());
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
