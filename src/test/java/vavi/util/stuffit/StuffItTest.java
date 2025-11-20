/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.stuffit;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


@PropsEntity(url = "file:local.properties")
class StuffItTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "sit")
    String sit = "src/test/resources/test.sit";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    void test() throws Exception {
        StuffIt stuffIt = new StuffIt(new String[] { sit });
    }
}
