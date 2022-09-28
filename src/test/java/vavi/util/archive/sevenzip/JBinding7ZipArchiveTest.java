/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * JBinding7ZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class JBinding7ZipArchiveTest {

    @Test
    void test0() throws Exception {
        SevenZip.initSevenZipFromPlatformJAR();
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            System.err.println(archiveFormat.getMethodName());
        }
    }

    static class MyExtractCallback implements IArchiveExtractCallback {
        private boolean skipExtraction;
        private IInArchive archive;

        public MyExtractCallback(IInArchive inArchive) {
            this.archive = inArchive;
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
            skipExtraction = (Boolean) archive.getProperty(index, PropID.IS_FOLDER);
            if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT) {
                return null;
            }
Debug.println("[" + index + "]: " + archive.getProperty(index, PropID.PATH));
            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    return data.length; // Return amount of proceed data
                }
            };
        }

        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        }

        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            if (skipExtraction) {
                return;
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                System.err.println("Extraction error");
            }
        }

        public void setCompleted(long completeValue) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }
    }

    @Test
    void test01() throws Exception {
        String file = "src/test/resources/rar5.rar";
        // null: autodetect archive type
        IInArchive inArchive = SevenZip.openInArchive(null,
                new SeekableByteChannelInStream(Files.newByteChannel(Paths.get(file))));

        int[] in = new int[inArchive.getNumberOfItems()];
        for (int i = 0; i < in.length; i++) {
            in[i] = i;
        }
        // false: Non-test mode
        inArchive.extract(in, false, new MyExtractCallback(inArchive));
    }

    @Test
    void test() throws Exception {
        Archive archive = new JBinding7ZipArchive(new File("src/test/resources/rar5.rar"));
        int c = 0;
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
            c++;
        }
        assertEquals(2, c);
    }

    @Test
    void test2() throws Exception {
Debug.println(Arrays.toString(Archives.getReaderFileSuffixes()));
        Archive archive = new JBinding7ZipArchive(new File("src/test/resources/rar5.rar"));
        int c = 0;
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
            c++;
            if (!e.isDirectory()) {
                Scanner s = new Scanner(archive.getInputStream(e));
                while (s.hasNextLine()) {
                    System.out.println(e.getName() + ": " + s.nextLine());
                }
            }
        }
        assertEquals(2, c);
    }
}

/* */
