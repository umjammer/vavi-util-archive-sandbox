/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;


/**
 * D88Test.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/06/08 umjammer initial version <br>
 */
public class D88Test {

    @Test
    public void test() throws IOException {
        InputStream is = new BufferedInputStream(D88Test.class.getResourceAsStream("/test.d88"));

        D88 d88 = D88.readFrom(is);
System.err.print(d88.getHeader());
        for (int i = 0; i < 164; i++) {
            if (d88.getTracks()[i] != null) {
//System.err.print(d88.getTracks()[i]);
            }
        }
    }

    /** */
    public static void main(String[] args) throws Exception {

        InputStream is = new BufferedInputStream(new FileInputStream(args[0]));

        D88 d88 = D88.readFrom(is);
System.err.print(d88.getHeader());
        for (int i = 0; i < 164; i++) {
            if (d88.getTracks()[i] != null) {
System.err.print(d88.getTracks()[i]);
            }
        }
    }
}

/* */
