/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.asar;

import vavi.util.archive.Entry;

import asar.VirtualFile;


/**
 * ASAR 圧縮のサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/14 umjammer initial version <br>
 */
public class AsarEntry implements Entry {

    /** */
    private VirtualFile entry;

    /** */
    public AsarEntry(VirtualFile entry) {
        this.entry = entry;
    }

    /**
     * エントリのコメント文字列を返します。
     */
    public String getComment() {
        return null;
    }

    /**
     * 圧縮されたエントリデータのサイズを返します。
     */
    public long getCompressedSize() {
        return entry.getSize();
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    public long getCrc() {
        return -1;
    }

    /**
     * エントリの補足フィールドデータを返します。
     */
    public Object getExtra() {
        return null;
    }

    /**
     * エントリの圧縮メソッドを返します。
     */
    public int getMethod() {
        return -1;
    }

    /**
     * エントリの名前を返します。
     */
    public String getName() {
        return entry.getPath();
    }

    /**
     * エントリデータの圧縮解除時のサイズを返します。
     */
    public long getSize() {
        return entry.getSize();
    }

    /**
     * エントリの修正時間を返します。
     */
    public long getTime() {
        return -1;
    }

    /**
     * これがディレクトリエントリである場合に、true を返します。
     */
    public boolean isDirectory() {
        return false;
    }

    /**
     * エントリに任意指定のコメント文字列を設定します。
     */
    public void setComment(String comment) {
    }

    /**
     * 圧縮されたエントリデータのサイズを設定します。
     */
    public void setCompressedSize(long csize) {
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを設定します。
     */
    public void setCrc(long crc) {
    }

    /**
     * エントリに任意指定の補足フィールドデータを設定します。
     */
    public void setExtra(Object extra) {
    }

    /**
     * エントリの圧縮メソッドを設定します。
     */
    public void setMethod(int method) {
    }

    /**
     * エントリデータの圧縮解除時のサイズを設定します。
     */
    public void setSize(long size) {
    }

    /**
     * エントリの修正時間を設定します。
     */
    public void setTime(long time) {
    }

    /**
     * このエントリのコピーを返します。
     */
    public Object clone() {
        return null;
    }

    /** */
    public Object getWrappedObject() {
        return entry;
    }
}

/* */
