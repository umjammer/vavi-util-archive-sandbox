/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archiveR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-22 nsano initial version <br>
 */
public class Test1 {

    @Test
    void test() throws Exception {
        // encode

        String infile = "src/test/resources/aesop.txt";
        String outfile = "tmp/test.out";
        String outfile2 = "tmp/test2.out";

        Path inPath = Paths.get(infile);
        Path outPath = Paths.get(outfile);
        Path outPath2 = Paths.get(outfile2);

        InputStream is = Files.newInputStream(inPath);
Debug.println("in: " + Files.size(inPath));
        ByteArrayOutputStream pos = new ByteArrayOutputStream();

        BurrowsWheeler.transform(is, pos);
Debug.println("middle1: " + pos.size());
        assertEquals(191947, pos.size());

        InputStream pis = new ByteArrayInputStream(pos.toByteArray());
        pos.reset();

        MoveToFront moveToFront = new MoveToFront();
        moveToFront.encode(pis, pos);
Debug.println("middle2: " + pos.size());

        pis = new ByteArrayInputStream(pos.toByteArray());
        OutputStream os = Files.newOutputStream(outPath);

        Huffman.compress(pis, os);

        assertTrue(Files.exists(outPath));
Debug.println("out: " + Files.size(outPath));
        assertEquals(66026, Files.size(outPath));

        // decode

        is = Files.newInputStream(outPath);
        pos.reset();

        Huffman.expand(is, pos);
Debug.println("middle3: " + pos.size());

        pis = new ByteArrayInputStream(pos.toByteArray());
        pos.reset();

        moveToFront.decode(pis, pos);
Debug.println("middle4: " + pos.size());

        pis = new ByteArrayInputStream(pos.toByteArray());
        os = Files.newOutputStream(outPath2);

        BurrowsWheeler.inverseTransform(pis, os);

        assertTrue(Files.exists(outPath2));
Debug.println("out2: " + Files.size(outPath2));

        assertNotEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath));
        assertEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath2));
    }

    @Test
    @Disabled("wip")
    void test2() throws Exception {
        String infile = "src/test/resources/aesop.txt";
        String outfile = "tmp/test.out";
        String outfile2 = "tmp/test2.out";

        Path inPath = Paths.get(infile);
        Path outPath = Paths.get(outfile);
        Path outPath2 = Paths.get(outfile2);

        InputStream is = Files.newInputStream(inPath);
Debug.println("in: " + Files.size(inPath));
        ByteArrayOutputStream pos = new ByteArrayOutputStream();

        BurrowsWheeler.transform(is, pos);
Debug.println("middle1: " + pos.size());
        assertEquals(191947, pos.size());

        is = new MoveToFront.EncodeInputStream(new ByteArrayInputStream(pos.toByteArray()));
        OutputStream os = Files.newOutputStream(outPath);

        Huffman.compress(is, os);

        assertTrue(Files.exists(outPath));
Debug.println("out: " + Files.size(outPath));
        assertEquals(66026, Files.size(outPath)); // TODO xxx

        // decode

        is = Files.newInputStream(outPath);
        pos.reset();

        Huffman.expand(is, pos);
Debug.println("middle3: " + pos.size());
        InputStream pis = new ByteArrayInputStream(pos.toByteArray());
        os = new MoveToFront.DecodeOutputStream(Files.newOutputStream(outPath2));

        BurrowsWheeler.inverseTransform(pis, os);

        assertTrue(Files.exists(outPath2));
Debug.println("out2: " + Files.size(outPath2));

        assertNotEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath));
        assertEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath2));
    }
}
