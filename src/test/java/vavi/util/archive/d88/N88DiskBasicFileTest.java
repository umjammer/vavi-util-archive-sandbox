/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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


/**
 * N88DiskBasicFileTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/06/08 umjammer initial version <br>
 */
@PropsEntity(url = "file:local.properties")
public class N88DiskBasicFileTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "test.d88")
    String file = "src/test/resources/test.d88";

    @BeforeEach
    void setup() throws IOException {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    @DisplayName("direct")
    public void test() throws IOException {
        N88DiskBasicFile disk = new N88DiskBasicFile(N88DiskBasicFileTest.class.getResourceAsStream("/test.d88"));
Debug.println(disk);

        for (Entry e : disk.entries()) {
            N88DiskBasicEntry entry = (N88DiskBasicEntry) e;
            System.err.println(entry.getName());
        }

        assertEquals(19, disk.entries().length);
    }

    @Test
    @DisplayName("spi")
    public void test2() throws IOException {
        Archive disk = Archives.getArchive(Paths.get(file).toFile());
Debug.println(disk);

        for (Entry e : disk.entries()) {
            N88DiskBasicEntry entry = (N88DiskBasicEntry) e;
            System.err.println(entry.getName());
        }

        assertEquals(19, disk.entries().length);
    }

    /**
     * java N88DiskBasicFile file
     */
    public static void main(String[] args) throws Exception {
        N88DiskBasicFileTest app = new N88DiskBasicFileTest();
        PropsEntity.Util.bind(app);
        app.t2(args);
    }

    void t1(String[] args) throws Exception {
        N88DiskBasicFile disk = new N88DiskBasicFile(args[0]);
//System.err.println(disk);

        Path dir = Paths.get(args[1]);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        for (Entry e : disk.entries()) {
            String name = e.getName();
            if (name.endsWith(".IMG")) {
                Path file = Paths.get(args[1], name);
System.err.println(name + " -> " + file);
                Files.copy(disk.getInputStream(e), file);
            }
        }
    }

    /**
     * java N88DiskBasicFile file
     */
    void t2(String[] args) throws Exception {
        String tmp = "tmp";

        N88DiskBasicFile disk = new N88DiskBasicFile(file);
System.err.println(disk);
//if (true) { return; }

        Path dir = Paths.get(tmp);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        for (Entry e : disk.entries()) {
            String name = e.getName();
            if (name.equals("godzi.IMG")) {
                Path file = Paths.get(tmp, name);
System.err.println(name + " -> " + file);
                Files.copy(disk.getInputStream(e), file);
            }
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void testX() throws IOException {
        Path p = Paths.get(file);
        Path outDir = Paths.get("tmp", p.getFileName().toString().replaceFirst("\\.d88$", ""));
        Files.createDirectories(outDir);

System.err.println("---- " + p);
        Archive disk = new N88DiskBasicFile(p.toAbsolutePath().toString());
        for (Entry e : disk.entries()) {
            Path file = outDir.resolve(e.getName());
System.err.println(e.getName() + " -> " + file);
            Files.copy(disk.getInputStream(e), file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

/* */
