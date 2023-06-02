/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import com.anatawa12.asar4j.AsarEntry;
import vavi.util.archive.WrappedEntry;


/**
 * Represents an ASAR archive file entry using asar4j.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/24 umjammer initial version <br>
 */
public class Asar4jEntry implements WrappedEntry<AsarEntry> {

    /** */
    private AsarEntry entry;

    /** */
    public Asar4jEntry(AsarEntry entry) {
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
    public AsarEntry getWrappedObject() {
        return entry;
    }
}

/* */
