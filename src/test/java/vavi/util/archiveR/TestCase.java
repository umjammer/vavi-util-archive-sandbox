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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-22 nsano initial version <br>
 */
public class TestCase {

    @Test
    @DisplayName("use encode/decode")
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BurrowsWheeler.transform(is, baos);
Debug.println("middle1: " + baos.size());
        assertEquals(191947, baos.size());

        InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();

        MoveToFront moveToFront = new MoveToFront();
        moveToFront.encode(bais, baos);
Debug.println("middle2: " + baos.size());

        bais = new ByteArrayInputStream(baos.toByteArray());
        OutputStream os = Files.newOutputStream(outPath);

        Huffman.compress(bais, os);

        assertTrue(Files.exists(outPath));
Debug.println("out: " + Files.size(outPath));
        assertEquals(66026, Files.size(outPath));

        // decode

        is = Files.newInputStream(outPath);
        baos.reset();

        Huffman.expand(is, baos);
Debug.println("middle3: " + baos.size());

        bais = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();

        moveToFront.decode(bais, baos);
Debug.println("middle4: " + baos.size());

        bais = new ByteArrayInputStream(baos.toByteArray());
        os = Files.newOutputStream(outPath2);

        BurrowsWheeler.inverseTransform(bais, os);

        assertTrue(Files.exists(outPath2));
Debug.println("out2: " + Files.size(outPath2));

        assertNotEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath));
        assertEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath2));
    }

    @Test
    @DisplayName("try input/output-stream")
    void test2() throws Exception {
        String infile = "src/test/resources/aesop.txt";
        String outfile = "tmp/test.out";
        String outfile2 = "tmp/test2.out";

        Path inPath = Paths.get(infile);
        Path outPath = Paths.get(outfile);
        Path outPath2 = Paths.get(outfile2);

Debug.println("in: " + Files.size(inPath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        OutputStream os = new BurrowsWheeler.TransformOutputStream(baos);
        Files.copy(inPath, os);
        os.close();
Debug.println("middle1: " + baos.size());
        assertEquals(191947, baos.size());

        InputStream is = new MoveToFront.EncodeInputStream(new ByteArrayInputStream(baos.toByteArray()));
        os = Files.newOutputStream(outPath);

        Huffman.compress(is, os);

        assertTrue(Files.exists(outPath));
Debug.println("out: " + Files.size(outPath));
        assertEquals(66026, Files.size(outPath));

        // decode

        is = Files.newInputStream(outPath);
        baos.reset();

        os = new MoveToFront.DecodeOutputStream(baos);
        Huffman.expand(is, os);
Debug.println("middle3: " + baos.size());

        InputStream pis = new ByteArrayInputStream(baos.toByteArray());
        os = Files.newOutputStream(outPath2);

        BurrowsWheeler.inverseTransform(pis, os);

        assertTrue(Files.exists(outPath2));
Debug.println("out2: " + Files.size(outPath2));

        assertNotEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath));
        assertEquals(Checksum.getChecksum(inPath), Checksum.getChecksum(outPath2));
    }
}
