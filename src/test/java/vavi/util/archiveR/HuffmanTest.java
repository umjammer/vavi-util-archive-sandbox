/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.StringUtil;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * HuffmanTest.
 */
public class HuffmanTest {

    @Test
    void test1() throws Exception {
        Path in = Path.of("src/test/resources/aesop.txt");
        Path out = Path.of("tmp", "out.huf");
        if (!Files.exists(out.getParent())) Files.createDirectories(out.getParent());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Huffman.compress(Files.newInputStream(in), baos);
//Debug.println(baos.size() + "\n" + StringUtil.getDump(baos.toByteArray()));
        Huffman.expand(new ByteArrayInputStream(baos.toByteArray()), Files.newOutputStream(out));

        assertEquals(Checksum.getChecksum(in), Checksum.getChecksum(out));
    }

    /**
     * Sample client that calls {@code compress()} if the command-line
     * argument is "-" an {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) Huffman.compress(System.in, System.out);
        else if (args[0].equals("+")) Huffman.expand(System.in, System.out);
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
