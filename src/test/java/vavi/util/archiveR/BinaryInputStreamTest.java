/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archiveR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;


/**
 * BinaryInputStreamTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-22 nsano initial version <br>
 */
public class BinaryInputStreamTest {

    @Test
    void test1() throws Exception {
        Path path = Paths.get("src/test/resources/logging.properties");
        BinaryInputStream in = new BinaryInputStream(Files.newInputStream(path));
        BinaryOutputStream out = new BinaryOutputStream(System.out);
        // read one 8-bit char at a time
        while (!in.isEmpty()) {
            char c = in.readChar();
            out.write(c);
        }
        out.flush();
    }

    /**
     * Test client. Reads in a binary input file from standard input and writes
     * it to standard output.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        BinaryInputStream in = new BinaryInputStream(System.in);
        BinaryOutputStream out = new BinaryOutputStream(System.out);
        // read one 8-bit char at a time
        while (!in.isEmpty()) {
            char c = in.readChar();
            out.write(c);
        }
        out.flush();
    }
}
