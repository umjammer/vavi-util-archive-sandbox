/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.condition.EnabledIf;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * PureJavaCabArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/04/08 umjammer initial version <br>
 */
@PropsEntity(url = "file:local.properties")
class PureJavaCabArchiveTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "foo.bar")
    String dir = "src/test/resources";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    String cab = "src/test/resources/test.cab";

    @Test
    @Disabled("unsupported compression type: 3")
    void test() throws Exception {
Debug.println(cab);
        Archive archive = new PureJavaCabArchive(Files.newInputStream(Path.of(cab)));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("spi")
    void test2() throws Exception {
Debug.println(cab);
        Archive archive = Archives.getArchive(Path.of(cab).toFile());
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("extract")
    @Disabled("unsupported compression type: 3")
    void test3() throws Exception {
Debug.println(cab);
        Archive archive = new PureJavaCabArchive(Path.of(cab).toFile());
        Entry entry = archive.entries()[0];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
Debug.println(is.available());
        Path out = Paths.get("tmp/out_purejavacab/" + entry.getName());
        if (!Files.exists(out.getParent())) Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    @Disabled("unsupported compression type: 3")
    void test5() throws Exception {
        Archive archive = new PureJavaCabArchive(Files.newInputStream(Path.of(cab)));
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
