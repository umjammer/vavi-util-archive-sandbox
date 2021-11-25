/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import asar.VirtualFile;


/**
 * ASAR アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/14 umjammer initial version <br>
 */
public class AsarArchive implements Archive {

    /** */
    private asar.AsarArchive archive;

    private String name;

    private long size;

    /** */
    public AsarArchive(File file) throws IOException {
        this.archive = new asar.AsarArchive(file);
        this.name = file.getName();
        this.size = file.length();
    }

    /**
     * ファイルを閉じます。
     */
    public void close() throws IOException {
        archive.close();
    }

    /**
     * ファイルエントリの列挙を返します。
     */
    public Entry[] entries() {
        List<Entry> entries = new ArrayList<>();
        for (VirtualFile e : archive) {
            entries.add(new AsarEntry(e));
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    /**
     * 指定された名前の ZIP ファイルエントリを返します。
     * 見つからない場合は null を返します。
     */
    public Entry getEntry(String name) {
        for (VirtualFile e : archive) {
            if (name.equals(e.getPath())) {
                return new AsarEntry(e);
            }
        }
        return null;
    }

    /**
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを
     * 返します。
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        for (VirtualFile e : archive) {
            if (entry.getName().equals(e.getPath())) {
                return new ByteArrayInputStream(e.read());
            }
        }
        return null;
    }

    /**
     * ファイルのパス名を返します。
     */
    public String getName() {
        return name;
    }

    /**
     * ファイル中のエントリの数を返します。
     */
    public int size() {
        return (int) size;
    }
}

/* */
