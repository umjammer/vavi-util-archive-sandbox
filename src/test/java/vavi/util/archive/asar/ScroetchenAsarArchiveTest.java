/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.asar;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.condition.EnabledIf;
import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * ScroetchenAsarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/16 umjammer initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
class ScroetchenAsarArchiveTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "test.asar")
    String asar = "src/test/resources/test.asar";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    // TODO src/test/resources/test.asar doesn't work
    @Test
    void test1() throws Exception {
        ScroetchenAsarArchive archive = new ScroetchenAsarArchive(new File(asar));
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
        }
    }

    @Test
    void test2() throws Exception {
        Archive archive = Archives.getArchive(new File(asar));
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
        }
    }

    /**
     * @param args archive output_directory
     */
    public static void main(String[] args) throws Exception {
        ScroetchenAsarArchive archive = new ScroetchenAsarArchive(new File(args[0]));
        for (Entry e : archive.entries()) {
            String name = e.getName();
            Path file = Paths.get(args[1], name);
System.err.println(name + " -> " + file);
            if (!Files.exists(file.getParent())) {
                Files.createDirectories(file.getParent());
            }
            Files.copy(archive.getInputStream(e), file);
        }
    }
}

/* */
