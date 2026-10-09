/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * MoveToFrontTest.
 */
public class MoveToFrontTest {

    @Test
    void test1() throws Exception {
        Path in = Path.of("src/test/resources/aesop.txt");
        Path out = Path.of("tmp", "out.mtf");
        if (!Files.exists(out.getParent())) Files.createDirectories(out.getParent());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        MoveToFront moveToFront = new MoveToFront();
        moveToFront.encode(Files.newInputStream(in), baos);
//Debug.println(baos.size() + "\n" + StringUtil.getDump(baos.toByteArray()));
        moveToFront.decode(new ByteArrayInputStream(baos.toByteArray()), Files.newOutputStream(out));

        assertEquals(Checksum.getChecksum(in), Checksum.getChecksum(out));
    }

    @Test
    void test2() throws Exception {
        Path in = Path.of("src/test/resources/aesop.txt");
        Path out = Path.of("tmp", "mtf.out");
        if (!Files.exists(out.getParent())) Files.createDirectories(out.getParent());

        InputStream is = new MoveToFront.EncodeInputStream(Files.newInputStream(in));
        OutputStream os =  new MoveToFront.DecodeOutputStream(Files.newOutputStream(out));
        byte[] buf = new byte[4096];
        while (true) {
            int r = is.read(buf);
            if (r < 0) break;
//Debug.println("\n" + StringUtil.getDump(buf, r));
            os.write(buf, 0, r);
        }
        os.flush();
        os.close();
Debug.println("out: " + Files.size(out));

        assertEquals(Checksum.getChecksum(in), Checksum.getChecksum(out));
    }

    /**
     * @param args if args[0] is '-', apply move-to-front encoding
     *             if args[0] is '+', apply move-to-front decoding
     */
    public static void main(String[] args) throws IOException {
        MoveToFront moveToFront = new MoveToFront();
        if (args[0].equals("-")) moveToFront.encode(System.in, System.out);
        else if (args[0].equals("+")) moveToFront.decode(System.in, System.out);
    }
}
