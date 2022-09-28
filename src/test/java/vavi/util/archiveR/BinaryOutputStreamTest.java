/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
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
 * BinaryInputStreamTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-22 nsano initial version <br>
 */
public class BinaryOutputStreamTest {

    @Test
    void test1() throws Exception {
        Path path = Paths.get("src/test/resources/logging.properties");
        BinaryInputStream in = new BinaryInputStream(Files.newInputStream(path));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputStream out = new BinaryOutputStream(baos);
        // write n integers to binary standard output
        for (int i = 0; i < in.available(); i++) {
            out.write(i);
        }
        out.flush();
        out.close();
Debug.println(baos.size() + "\n" + StringUtil.getDump(baos.toByteArray()));
    }

    /**
     * Tests the methods in this class.
     *
     * @param args the command-line arguments
     */
    public void main(String[] args) {
        int m = Integer.parseInt(args[0]);
        BinaryOutputStream out = new BinaryOutputStream(System.out);
        // write n integers to binary standard output
        for (int i = 0; i < m; i++) {
            out.write(i);
        }
        out.flush();
    }
}
