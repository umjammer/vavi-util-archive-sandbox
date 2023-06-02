/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import vavi.util.archive.WrappedEntry;

import asar.VirtualFile;


/**
 * Represents an ASAR archive file entry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/14 umjammer initial version <br>
 */
public class ScroetchenAsarEntry implements WrappedEntry<VirtualFile> {

    /** */
    private VirtualFile entry;

    /** */
    public ScroetchenAsarEntry(VirtualFile entry) {
        this.entry = entry;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long getCompressedSize() {
        return entry.getSize();
    }

    @Override
    public long getCrc() {
        return -1;
    }

    @Override
    public Object getExtra() {
        return null;
    }

    @Override
    public int getMethod() {
        return -1;
    }

    @Override
    public String getName() {
        return entry.getPath();
    }

    @Override
    public long getSize() {
        return entry.getSize();
    }

    @Override
    public long getTime() {
        return -1;
    }

    @Override
    public boolean isDirectory() {
        return false;
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
    public VirtualFile getWrappedObject() {
        return entry;
    }
}

/* */
