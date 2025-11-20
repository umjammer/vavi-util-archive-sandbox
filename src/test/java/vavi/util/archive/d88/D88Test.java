/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;


/**
 * D88Test.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/06/08 umjammer initial version <br>
 */
@PropsEntity(url = "file:local.properties")
class D88Test {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "test.d88")
    String d88 = "src/test/resources/test.d88";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    void test() throws IOException {
        InputStream is = new BufferedInputStream(Files.newInputStream(Path.of(d88)));

        D88 d88 = D88.readFrom(is);
System.err.print(d88.getHeader());
        for (int i = 0; i < 164; i++) {
            if (d88.getTracks()[i] != null) {
System.err.print(d88.getTracks()[i]);
            }
        }
    }

    /** */
    public static void main(String[] args) throws Exception {
        D88Test app = new D88Test();
        app.setup();
        if (args.length > 0)
            app.d88 = args[0];
        app.test();
    }
}
