/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * ApacheArjArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/17 umjammer initial version <br>
 */
class ApacheArjArchiveTest {

    @Test
    @DisplayName("direct")
    void test() throws Exception {
        ApacheArjArchive arj = new ApacheArjArchive(new File("src/test/resources/test.arj"));
System.err.println("size: " + arj.size());
        Arrays.stream(arj.entries()).forEach(e -> System.err.println(e.getName()));
        Entry entry = arj.getEntry("UNZIP4D.BTM");
System.err.println("entry: " + entry.getSize());
        InputStream is = arj.getInputStream(entry);
System.err.println("is: " + is.available());
    }

    @Test
    @DisplayName("spi, file")
    void test1() throws Exception {
        Archive arj = Archives.getArchive(new File("src/test/resources/test.arj"));
System.err.println("size: " + arj.size());
        Arrays.stream(arj.entries()).forEach(e -> System.err.println(e.getName()));
        Entry entry = arj.getEntry("UNZIP4D.BTM");
System.err.println("entry: " + entry.getSize());
        InputStream is = arj.getInputStream(entry);
System.err.println("is: " + is.available());
    }

    @Test
    @DisplayName("spi, input stream")
    void test2() throws Exception {
        Archive arj = Archives.getArchive(new BufferedInputStream(Files.newInputStream(Paths.get("src/test/resources/test.arj"))));
System.err.println("size: " + arj.size());
        Arrays.stream(arj.entries()).forEach(e -> System.err.println(e.getName()));
        Entry entry = arj.getEntry("UNZIP4D.BTM");
System.err.println("entry: " + entry.getSize());
        InputStream is = arj.getInputStream(entry);
System.err.println("is: " + is.available());
    }

    @Test
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new ApacheArjArchive(new File("src/test/resources/test.arj"));
        Entry entry = archive.entries()[0];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
Debug.println(is.available());
        Path out = Paths.get("tmp/out_jbinding7z/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        Archive archive = new ApacheArjArchive(new URL("file:src/test/resources/test.arj").openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName() + ", " + entry.getSize());
        }
Debug.println("entries after loop: " + archive.size());
        assertNotEquals(0, archive.size());
        // TODO available is 0
Debug.println("stream after loop: " + archive.entries()[0].getName() + ", first byte: " + archive.getInputStream(archive.entries()[0]).read());
        assertNotNull(archive.getInputStream(archive.entries()[0]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory() && archive.getInputStream(entry).read() > 0) {
Debug.println("stream after loop: 2nd byte: " + archive.getInputStream(entry).read());
                return;
            }
        }
        fail("no file size > 0");
    }
}

/* */
