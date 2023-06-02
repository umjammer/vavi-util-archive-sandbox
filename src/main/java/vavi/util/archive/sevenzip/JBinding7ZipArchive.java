/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;


/**
 * A service provider for 7z archive file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/10/07 umjammer initial version <br>
 */
public class JBinding7ZipArchive extends InputStreamSupport implements Archive {

    /** */
    private ISimpleInArchive archive;

    /** */
    private String name;

    /** */
    private Entry[] entries;

    /** */
    public JBinding7ZipArchive(File file) throws IOException {
        IInArchive archive_ = SevenZip.openInArchive(null,
                new SeekableByteChannelInStream(Files.newByteChannel(file.toPath())));
        this.archive = archive_.getSimpleInterface();
        this.name = file.getPath();
    }

    /** */
    public JBinding7ZipArchive(InputStream is) throws IOException {
        super(is);
        IInArchive archive_ = SevenZip.openInArchive(null,
                new SeekableByteChannelInStream(Files.newByteChannel(archiveFileForInputStream.toPath())));
        this.archive = archive_.getSimpleInterface();
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
                for (ISimpleInArchiveItem e : archive.getArchiveItems()) {
                    entries.add(new JBinding7ZipEntry(e));
                }
                this.entries = entries.toArray(new Entry[0]);
            } catch (SevenZipException e) {
                throw new IllegalStateException(e);
            }
        }
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries()) {
Debug.println("@@@: " + name + ", " + entry.getName());
            if (name.equals(entry.getName())) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        for (ISimpleInArchiveItem e : archive.getArchiveItems()) {
            if (entry.getName().equals(e.getPath())) {
                return new InputStream() {
                    boolean done;
                    BlockingDeque<Integer> deque = new LinkedBlockingDeque<>();
                    {
                        ExtractOperationResult result = e.extractSlow(data -> {
//Debug.println("data: " + data.length);
                            for (byte datum : data) {
                                deque.add(datum & 0xff);
                            }
                            return data.length;
                        });
//Debug.println("extractSlow: " + result);
                        deque.add(-1); // poison pill
                    }
                    @Override
                    public int read() throws IOException {
                        try {
//Debug.println("read: " + (deque.peek() != null ? deque.peek() : "none"));
                            int r = -1;
                            if (!done) {
                                r = deque.take();
                            }
                            if (r == -1) {
                                done = true;
                            }
                            return r;
                        } catch (InterruptedException ex) {
Debug.println(Level.FINE, "interrupted: who cad do this? i want to do this");
                            return -1;
                        }
                    }
                };
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
