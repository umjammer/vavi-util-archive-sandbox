/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.InputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * ComRarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class ComRarArchiveTest {

    @Test
    @Disabled
    void test() {
        fail("Not yet implemented");
    }

    //----

    /** */
    public static void main(String[] args) throws Exception {
        ComRarArchive rar = new ComRarArchive(new File(args[0]));
        Entry<?> entry = rar.getEntry(args[1]);
System.err.println("entry: " + entry);
        InputStream is = rar.getInputStream(entry);
System.err.println("is: " + is);
    }

}

/* */
