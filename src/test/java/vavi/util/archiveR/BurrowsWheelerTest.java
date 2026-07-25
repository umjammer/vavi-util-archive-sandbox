/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.StringUtil;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * BurrowsWheelerTest.
 */
public class BurrowsWheelerTest {

    @Test
    void test1() throws Exception {
        Path in = Paths.get("src/test/resources/aesop.txt");
        Path out = Path.of("tmp", "out.bwt");
        if (!Files.exists(out.getParent())) Files.createDirectories(out.getParent());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BurrowsWheeler.transform(Files.newInputStream(in), baos);
//Debug.println(baos.size() + "\n" + StringUtil.getDump(baos.toByteArray()));
        BurrowsWheeler.inverseTransform(new ByteArrayInputStream(baos.toByteArray()), Files.newOutputStream(out));

        assertEquals(Checksum.getChecksum(in), Checksum.getChecksum(out));
    }

    @Test
    void test2() throws Exception {
        Path in = Paths.get("src/test/resources/aesop.txt");
        Path out = Path.of("tmp", "bwt.out");
        if (!Files.exists(out.getParent())) Files.createDirectories(out.getParent());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = new BurrowsWheeler.TransformOutputStream(baos);
        Files.copy(in, os);
        os.close();

        // the streaming transform must agree with the all at once one
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        BurrowsWheeler.transform(Files.newInputStream(in), expected);
        assertArrayEquals(expected.toByteArray(), baos.toByteArray());

        BurrowsWheeler.inverseTransform(new ByteArrayInputStream(baos.toByteArray()), Files.newOutputStream(out));

        assertEquals(Checksum.getChecksum(in), Checksum.getChecksum(out));
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) BurrowsWheeler.transform(System.in, System.out);
        else if (args[0].equals("+")) BurrowsWheeler.inverseTransform(System.in, System.out);
    }
}
