/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;


/**
 * ApacheArjArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/17 umjammer initial version <br>
 */
class ApacheArjArchiveTest {

    @Test
    void test() throws Exception {
        ApacheArjArchive arj = new ApacheArjArchive(new File("src/test/resources/test.arj"));
System.err.println("size: " + arj.size());
        Arrays.stream(arj.entries()).forEach(e -> System.err.println(e.getName()));
        Entry entry = arj.getEntry("UNZIP4D.BTM");
System.err.println("entry: " + entry.getSize());
        InputStream is = arj.getInputStream(entry);
System.err.println("is: " + is.available());
    }
}

/* */
