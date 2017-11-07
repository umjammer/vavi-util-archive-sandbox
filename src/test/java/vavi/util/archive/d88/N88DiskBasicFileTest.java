/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.InputStream;

import org.junit.Test;

import vavi.util.archive.Entry;

import static org.junit.Assert.fail;


/**
 * N88DiskBasicFileTest. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/06/08 umjammer initial version <br>
 */
public class N88DiskBasicFileTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    //-------------------------------------------------------------------------

    /**
     * java N88DiskBasicFile file
     */
    public static void main(String[] args) throws Exception {

        N88DiskBasicFile disk = new N88DiskBasicFile(args[0]);
//System.err.println(disk);

        for (Entry e : disk.entries()) {
            int n = 0;
            N88DiskBasicEntry entry = (N88DiskBasicEntry) e;
            InputStream is = disk.getInputStream(entry);
//Debug.dump(is);
            top: while (true) {
                System.err.println("---- " + n++ + " ----");
                for (int y = 0; y < 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        int c = is.read();
                        if (c == -1) {
                            break top;
                        }
                        String s = "0" + Integer.toHexString(c).toUpperCase();
                        System.err.print(s.substring(s.length() - 2) + " ");
                    }
                    System.err.println();
                }
                System.err.println();
            }
        }
    }
}

/* */
