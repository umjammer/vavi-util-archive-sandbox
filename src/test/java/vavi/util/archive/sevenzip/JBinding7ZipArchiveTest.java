/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * JBinding7ZipArchiveTest.
 *
 * TODO sevenzipjbinding doesn't support arm64
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
@EnabledIfSystemProperty(named = "os.arch", matches = "x86_64") // TODO what we really need is jvm "process's" arch
@PropsEntity(url = "file:local.properties")
class JBinding7ZipArchiveTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "file.rar1")
    String file1 = "src/test/resources/test.rar";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

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
            return data -> {
                return data.length; // Return amount of proceed data
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

    @Test
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new JBinding7ZipArchive(new File("src/test/resources/rar5.rar"));
        // TODO JBinding7ZipArchive#entries() contains directory (this [0] is dir)
        Entry entry = archive.entries()[0];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_jbinding7z/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("extract large")
    public void test31() throws Exception {
        Archive archive = new JBinding7ZipArchive(new File(file1));
        // TODO JBinding7ZipArchive#entries() contains directory (this [0] is dir)
        Entry entry = archive.entries()[0];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_jbinding7z/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        Archive archive = new JBinding7ZipArchive(new URL("file:src/test/resources/rar5.rar").openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName() + ", " + entry.getSize());
        }
Debug.println("entries after loop: " + archive.size());
        assertNotEquals(0, archive.size());
        // TODO JBinding7ZipArchive#entries() contains directory (this [0] is dir)
Debug.println("stream after loop: " + archive.entries()[0].getName() + ", firstByte: " + archive.getInputStream(archive.entries()[0]).read());
        assertNotNull(archive.getInputStream(archive.entries()[0]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory() && archive.getInputStream(entry).read() > -1) {
Debug.println("stream after loop: 2ndByte: " + archive.getInputStream(entry).read());
                return;
            }
        }
        fail("no file size > 0");
    }
}

/* */
