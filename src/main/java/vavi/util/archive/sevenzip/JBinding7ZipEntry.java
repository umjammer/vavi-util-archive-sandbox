/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.UncheckedIOException;

import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import vavi.util.archive.WrappedEntry;


/**
 * A service provider for 7z archive file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/10/07 umjammer initial version <br>
 */
public class JBinding7ZipEntry implements WrappedEntry<ISimpleInArchiveItem> {

    /** */
    private ISimpleInArchiveItem entry;

    /** */
    public JBinding7ZipEntry(ISimpleInArchiveItem entry) {
        this.entry = entry;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long getCompressedSize() {
        try {
            return entry.getPackedSize();
        } catch (SevenZipException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public long getCrc() {
        try {
            return entry.getCRC();
        } catch (SevenZipException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Object getExtra() {
        return null;
    }

    @Override
    public int getMethod() {
        try {
            return entry.getMethod().hashCode(); // TODO
        } catch (SevenZipException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String getName() {
        try {
            return entry.getPath();
        } catch (SevenZipException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public long getSize() {
        try {
            return entry.getSize();
        } catch (SevenZipException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public long getTime() {
        try {
            return entry.getCreationTime().getTime();
        } catch (SevenZipException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean isDirectory() {
        try {
            return entry.isFolder();
        } catch (SevenZipException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setComment(String comment) {
    }

    @Override
    public void setCompressedSize(long csize) {
    }

    @Override
    public void setCrc(long crc) {
    }

    @Override
    public void setExtra(Object extra) {
    }

    @Override
    public void setMethod(int method) {
    }

    @Override
    public void setSize(long size) {
    }

    @Override
    public void setTime(long time) {
    }

    @Override
    public Object clone() {
        return null;
    }

    @Override
    public ISimpleInArchiveItem getWrappedObject() {
        return entry;
    }
}

/* */
