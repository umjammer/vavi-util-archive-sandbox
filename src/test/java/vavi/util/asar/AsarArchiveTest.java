/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.asar;

import java.io.File;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;


/**
 * AsarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/16 umjammer initial version <br>
 */
class AsarArchiveTest {

    @Test
    void test() throws Exception {
        AsarArchive archive = new AsarArchive(new File("tmp/MYukkuriVoice-darwin-x64/MYukkuriVoice.app/Contents/Resources/electron.asar"));
        for (Entry e : archive.entries()) {
            System.err.println(e.getName());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
    }
}

/* */
