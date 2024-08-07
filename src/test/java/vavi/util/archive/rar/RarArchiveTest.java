/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import vavi.util.archive.Entry;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;
import vavi.util.rar.UnRar;


/**
 * RarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
@PropsEntity(url = "file:local.properties")
class RarArchiveTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "rar")
    String rar = "src/test/resources/test.rar";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    void test() throws Exception {
        UnRar unrar = new UnRar(new String[] { "l", rar });
    }

    @Test
    void test2() throws Exception {
        UnRar unrar = new UnRar(new String[] { "x", rar });
    }

    // ----

    /** */
    public static void main(String[] args) throws Exception {
        ComRarArchive rar = new ComRarArchive(new File(args[0]));
        Entry entry = rar.getEntry(args[1]);
System.err.println("entry: " + entry);
        InputStream is = rar.getInputStream(entry);
System.err.println("is: " + is);
    }
}
