/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import vavi.util.archive.Entry;


/**
 * N88 ディスクです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
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

    /** */
    protected N88DiskBasicEntry(String name,
                				int attribute,
                				int startCluster) {
    	this.name         = name;
    	this.attribute    = attribute;
    	this.startCluster = startCluster;
    }

    /** */
    public String getName() {
        return name;
    }

    /** */
    void print() {
System.err.print(name + ":" +
		 ((attribute & NOT_ASCII)        != 0 ? "*" : ".") +
		 ((attribute & READ_AFTER_WRITE) != 0 ? "*" : ".") +
		 ((attribute & P_OPTION_FILE)    != 0 ? "*" : ".") +
		 ((attribute & WRITE_PROTECT)    != 0 ? "*" : ".") +
		 "   " +
		 ((attribute & MACHINE_LANGUAGE) != 0 ? "*" : ".") +
		 ": " +
		 Integer.toHexString(startCluster));
    }

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public String getComment() {
        throw new UnsupportedOperationException("non sense");
    }

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public long getCompressedSize() {
        throw new UnsupportedOperationException("non sense");
    }

    /** @see vavi.util.archive.Entry#getCrc() */
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

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public int getMethod() {
        throw new UnsupportedOperationException("non sense");
    }

    /** @see vavi.util.archive.Entry#getSize() */
    public long getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    /** @see vavi.util.archive.Entry#getTime() */
    public long getTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public boolean isDirectory() {
        throw new UnsupportedOperationException("non sense");
    }

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public void setComment(String comment) {
        throw new UnsupportedOperationException("non sense");
    }

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public void setCompressedSize(long csize) {
        throw new UnsupportedOperationException("non sense");
    }

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
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

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public void setMethod(int method) {
        throw new UnsupportedOperationException("non sense");
    }

    /** @see vavi.util.archive.Entry#setSize(long) */
    public void setSize(long size) {
        // TODO Auto-generated method stub
    }

    /** @see vavi.util.archive.Entry#setTime(long) */
    public void setTime(long time) {
        // TODO Auto-generated method stub
    }

    /** @throws UnsupportedOperationException このメソッドはサポートされていません。 */
    public Object getWrappedObject() {
        throw new UnsupportedOperationException("non sense");
    }
}

/* */
