/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.xar;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import vavi.util.archive.Entry;
import vavi.util.archive.asar.AsarArchive;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * XarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/23 umjammer initial version <br>
 */
class XarArchiveTest {

    @Test
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
}

/* */
