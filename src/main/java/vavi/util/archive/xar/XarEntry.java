/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.xar;

import vavi.util.archive.WrappedEntry;


/**
 * A service provider which is processing XAR archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/10/07 umjammer initial version <br>
 */
public class XarEntry implements WrappedEntry<com.sprylab.xar.XarEntry> {

    /** */
    private com.sprylab.xar.XarEntry entry;

    /** */
    public XarEntry(com.sprylab.xar.XarEntry entry) {
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
        return entry.getName();
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
        return entry.isDirectory();
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
    public com.sprylab.xar.XarEntry getWrappedObject() {
        return entry;
    }
}

/* */
