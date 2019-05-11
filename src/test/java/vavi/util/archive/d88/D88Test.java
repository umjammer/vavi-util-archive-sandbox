/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * D88Test.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/06/08 umjammer initial version <br>
 */
public class D88Test {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    /** */
    public static void main(String[] args) throws Exception {

        InputStream is = new BufferedInputStream(new FileInputStream(args[0]));

        D88 d88 = D88.readFrom(is);
//d88.getHeader().print();
        for (int i = 0; i < 164; i++) {
            if (d88.getTracks()[i] != null) {
                d88.getTracks()[i].print();
            }
        }
    }
}

/* */
