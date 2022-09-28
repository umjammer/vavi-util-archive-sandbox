/*
 * https://github.com/prog-ai/ArchivR
 *
 * http://opensource.org/licenses/mit-license.php
 */

package vavi.util.archiveR;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * BurrowsWheelerTest.
 */
public class BurrowsWheelerTest {

    @Test
    void test1() throws Exception {
        Path path = Paths.get("src/test/resources/logging.properties");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BurrowsWheeler.transform(Files.newInputStream(path), baos);
Debug.println(baos.size() + "\n" + StringUtil.getDump(baos.toByteArray()));
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) BurrowsWheeler.transform(System.in, System.out);
        else if (args[0].equals("+")) BurrowsWheeler.inverseTransform(System.in, System.out);
    }
}
