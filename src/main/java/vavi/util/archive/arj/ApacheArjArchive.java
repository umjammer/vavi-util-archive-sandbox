/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.WrappedEntry;
import vavi.util.archive.apache.ApacheEntry;


/**
 * The wrapper for Apache commons compress.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 * @see "https://commons.apache.org/proper/commons-compress/examples.html"
 */
public class ApacheArjArchive implements Archive {

    /** */
    private File file;

    /** */
    private List<Entry> entries = new ArrayList<>();

    /** */
    public ApacheArjArchive(File file) throws IOException {
        this.file = file;

        try (ArchiveInputStream i = new ArjArchiveInputStream(new FileInputStream(file))) {
            ArchiveEntry entry = null;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
Debug.println("skip entry: " + entry.getName() + ", " + entry.getSize());
                    continue;
                }
                entries.add(new ApacheEntry(entry));
Debug.println("entry: " + entry.getName() + ", " + entry.getSize());
            }
        } catch (ArchiveException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Entry[] entries() {
        Entry[] results = new Entry[entries.size()];
        entries.toArray(results);
        return results;
    }

    @Override
    public Entry getEntry(String name) {
        return entries.stream().filter(e -> e.getName().equals(name)).findFirst().get();
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        try (ArchiveInputStream i = new ArjArchiveInputStream(new FileInputStream(file))) {
            ArchiveEntry e = null;
            while ((e = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(e)) {
Debug.println("skip entry: " + entry.getName() + ", " + entry.getSize());
                    continue;
                }
                if (WrappedEntry.class.cast(entry).getWrappedObject().equals(e)) {
                    return i;
                }
            }
        } catch (ArchiveException e) {
            throw new IOException(e);
        }
        throw new NoSuchElementException(entry.getName()); // TODO
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public int size() {
        return entries.size();
    }
}

/* */
