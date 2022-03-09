/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.util.Date;

import vavi.util.archive.Entry;


/**
 * Represents a N88 disk.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public class N88DiskBasicEntry implements Entry {

    /** */
    private static final int NOT_ASCII        = 0x80;
    /** */
    private static final int READ_AFTER_WRITE = 0x40;
    /** */
    private static final int P_OPTION_FILE    = 0x20;
    /** */
    private static final int WRITE_PROTECT    = 0x10;
    /** */
    private static final int MACHINE_LANGUAGE = 0x01;

    /** */
    private String name;
    /** @see #getExtra() */
    private int attribute;
    /** @see #getExtra() */
    private int startCluster;

    private long size;

    /** */
    protected N88DiskBasicEntry(String name, int attribute, int startCluster) {
        this.name = name;
        this.attribute = attribute;
        this.startCluster = startCluster;
    }

    @Override
    public String getName() {
        String[] pair = name.split("\\.");
        return pair[0].trim() + (pair[1].trim().length() > 0 ? "." + pair[1].trim() : "");
    }

    @Override
    public String toString() {
        return name + ":" +
                ((attribute & NOT_ASCII)        != 0 ? "*" : ".") +
                ((attribute & READ_AFTER_WRITE) != 0 ? "*" : ".") +
                ((attribute & P_OPTION_FILE)    != 0 ? "*" : ".") +
                ((attribute & WRITE_PROTECT)    != 0 ? "*" : ".") +
                "   " +
                ((attribute & MACHINE_LANGUAGE) != 0 ? "*" : ".") +
                ": " +
                Integer.toHexString(startCluster);
    }

    @Override
    public String getComment() {
        throw new UnsupportedOperationException("non sense");
    }

    @Override
    public long getCompressedSize() {
        throw new UnsupportedOperationException("non sense");
    }

    @Override
    public long getCrc() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return Integer[] 0: attribute, 1: startCluster
     */
    public Object getExtra() {
        return new int[] { attribute, startCluster };
    }

    @Override
    public int getMethod() {
        throw new UnsupportedOperationException("non sense");
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getTime() {
        return new Date().getTime();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public void setComment(String comment) {
        throw new UnsupportedOperationException("non sense");
    }

    @Override
    public void setCompressedSize(long csize) {
        throw new UnsupportedOperationException("non sense");
    }

    @Override
    public void setCrc(long crc) {
        throw new UnsupportedOperationException("non sense");
    }

    /**
     * @param extra int[] 0: attribute, 1: startCluster
     */
    public void setExtra(Object extra) {
        attribute = ((int[]) extra)[0];
        startCluster = ((int[]) extra)[1];
    }

    @Override
    public void setMethod(int method) {
        throw new UnsupportedOperationException("non sense");
    }

    @Override
    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public void setTime(long time) {
        // TODO Auto-generated method stub
    }
}

/* */
