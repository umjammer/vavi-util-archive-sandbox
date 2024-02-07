/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.xar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
 * XarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/23 umjammer initial version <br>
 */
class XarArchiveTest {

    @Test
    @DisplayName("direct")
    void test() throws Exception {
        String file = "src/test/resources/test.xar";
        XarArchive archive = new XarArchive(new File(file));
        int c = 0;
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
            c++;
        }
        assertEquals(9, c);
    }

    @Test
    @DisplayName("spi")
    void test1() throws Exception {
        String file = "src/test/resources/test.xar";
        Archive archive = Archives.getArchive(Paths.get(file).toFile());
        int c = 0;
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
            c++;
        }
        assertEquals(9, c);
    }

    @Test
    @DisplayName("spi")
    void test2() throws Exception {
        String file = "src/test/resources/test.xar";
        Archive archive = Archives.getArchive(new BufferedInputStream(Files.newInputStream(Paths.get(file))));
        int c = 0;
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
            c++;
        }
        assertEquals(9, c);
    }

    @Test
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new XarArchive(new File("src/test/resources/test.xar"));
        // TODO XarArchive#entries() contains directory (this [0~2] is dir)
        //  means it doesn't support available for directory
        Entry entry = archive.entries()[3];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_xar/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        Archive archive = new XarArchive(new URL("file:src/test/resources/test.xar").openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName() + ", " + entry.getSize());
        }
Debug.println("entries after loop: " + archive.size());
        assertNotEquals(0, archive.size());
        // TODO XarArchive#entries() contains directory (this [0~2] is dir)
        //  means it doesn't support available for directory
Debug.println("stream after loop: " + archive.entries()[3].getName() + ", available: " + archive.getInputStream(archive.entries()[3]).available());
        assertNotNull(archive.getInputStream(archive.entries()[3]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory() && archive.getInputStream(entry).available() > 0) {
Debug.println("stream after loop: available: " + archive.getInputStream(entry).available());
                return;
            }
        }
        fail("no file size > 0");
    }
}

/* */
