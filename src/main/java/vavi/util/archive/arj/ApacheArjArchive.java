/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;
import vavi.util.archive.WrappedEntry;
import vavi.util.archive.apache.ApacheEntry;


/**
 * Represents an ARJ archive file using apache commons-compress.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 * @see "https://commons.apache.org/proper/commons-compress/examples.html"
 */
public class ApacheArjArchive extends InputStreamSupport implements Archive {

    /** */
    private File file;

    /** */
    private Entry[] entries;

    /** */
    public ApacheArjArchive(File file) throws IOException {
        this.file = file;
    }

    /** */
    public ApacheArjArchive(InputStream is) throws IOException {
        super(is);
        this.file = archiveFileForInputStream;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Entry[] entries() {
        if (entries == null) {
            try (ArchiveInputStream i = new ArjArchiveInputStream(Files.newInputStream(file.toPath()))) {
                List<ApacheEntry> entries = new ArrayList<>();
                ArchiveEntry entry;
                while ((entry = i.getNextEntry()) != null) {
                    if (!i.canReadEntryData(entry)) {
Debug.println("skip entry: " + entry.getName() + ", " + entry.getSize());
                        continue;
                    }
                    entries.add(new ApacheEntry(entry));
Debug.println(Level.FINE, "entry: " + entry.getName() + ", " + entry.getSize());
                }
                this.entries = entries.toArray(new ApacheEntry[0]);
            } catch (ArchiveException | IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        return Arrays.stream(entries()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    /** WARNING: available does not work */
    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        try {
            ArchiveInputStream i = new ArjArchiveInputStream(Files.newInputStream(file.toPath()));
            ArchiveEntry e = null;
            while ((e = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(e)) {
Debug.println("skip entry: " + entry.getName() + ", " + entry.getSize());
                    continue;
                }
                if (((WrappedEntry<?>) entry).getWrappedObject().equals(e)) {
                    return i;
                }
            }
        } catch (ArchiveException e) {
            throw new IOException(e);
        }
        throw new IllegalArgumentException(entry.getName());
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public int size() {
        return entries().length;
    }
}

/* */
