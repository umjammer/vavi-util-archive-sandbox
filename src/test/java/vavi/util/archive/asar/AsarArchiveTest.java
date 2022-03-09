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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;


/**
 * AsarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/16 umjammer initial version <br>
 */
class AsarArchiveTest {

    @Test
    @Disabled
    void test() throws Exception {
        AsarArchive archive = new AsarArchive(new File("src/test/resources/test.asar"));
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
        }
    }

    /**
     * @param args archive output_directory
     */
    public static void main(String[] args) throws Exception {
        AsarArchive archive = new AsarArchive(new File(args[0]));
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
