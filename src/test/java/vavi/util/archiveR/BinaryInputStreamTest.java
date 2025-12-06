/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archiveR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * BinaryInputStreamTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-22 nsano initial version <br>
 */
public class BinaryInputStreamTest {

    @Test
    void test1() throws Exception {
        Path in = Paths.get("src/test/resources/aesop.txt");
        BinaryInputStream bin = new BinaryInputStream(Files.newInputStream(in));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputStream bout = new BinaryOutputStream(baos);
        // read one 8-bit char at a time
        while (!bin.isEmpty()) {
            byte b = bin.readByte();
            bout.write(b);
        }
        bout.flush();
        assertEquals(Checksum.getChecksum(in), Checksum.getChecksum(new ByteArrayInputStream(baos.toByteArray())));
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
