/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * MoveToFrontTest.
 */
public class MoveToFrontTest {

    @Test
    void test1() throws Exception {
        Path path = Paths.get("src/test/resources/logging.properties");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MoveToFront moveToFront = new MoveToFront();
        moveToFront.encode(Files.newInputStream(path), baos);
Debug.println(baos.size() + "\n" + StringUtil.getDump(baos.toByteArray()));
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
