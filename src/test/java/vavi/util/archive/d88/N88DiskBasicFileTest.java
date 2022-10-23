/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;


/**
 * N88DiskBasicFileTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/06/08 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class N88DiskBasicFileTest {

    @Test
    public void test() throws IOException {
        N88DiskBasicFile disk = new N88DiskBasicFile(N88DiskBasicFileTest.class.getResourceAsStream("/test.d88"));
System.err.println(disk);

        for (Entry e : disk.entries()) {
            N88DiskBasicEntry entry = (N88DiskBasicEntry) e;
            System.err.println(entry.getName());
        }

        assertEquals(19, disk.entries().length);
    }

    //----

    @Property(name = "test.d88")
    String file;

    /**
     * java N88DiskBasicFile file
     */
    public static void main(String[] args) throws Exception {
        N88DiskBasicFileTest app = new N88DiskBasicFileTest();
        PropsEntity.Util.bind(app);
        app.t2(args);
    }

    void t1(String[] args) throws Exception {
        N88DiskBasicFile disk = new N88DiskBasicFile(args[0]);
//System.err.println(disk);

        Path dir = Paths.get(args[1]);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        for (Entry e : disk.entries()) {
            String name = e.getName();
            if (name.endsWith(".IMG")) {
                Path file = Paths.get(args[1], name);
System.err.println(name + " -> " + file);
                Files.copy(disk.getInputStream(e), file);
            }
        }
    }

    /**
     * java N88DiskBasicFile file
     */
    void t2(String[] args) throws Exception {
        String tmp = "tmp";

        N88DiskBasicFile disk = new N88DiskBasicFile(file);
System.err.println(disk);
//if (true) { return; }

        Path dir = Paths.get(tmp);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        for (Entry e : disk.entries()) {
            String name = e.getName();
            if (name.equals("godzi.IMG")) {
                Path file = Paths.get(tmp, name);
System.err.println(name + " -> " + file);
                Files.copy(disk.getInputStream(e), file);
            }
        }
    }

    static Stream<Arguments> sources() throws IOException {
        return Files.list(Paths.get("/Users/nsano/src/java/mucomDotNET/tmp/MCM_sample_20190124")).filter(p -> p.toString().endsWith(".d88")).map(p -> arguments(p));
    }

    @Disabled("this is application, cause fuckin' intellij doesn't support run w/ test classpath")
    @ParameterizedTest
    @MethodSource("sources")
    public void testX(Path p) throws IOException {
        Path outDir = Paths.get("/Users/nsano/src/java/mucomDotNET/tmp/kodai", p.getFileName().toString().replaceFirst("\\.d88$", ""));
        Files.createDirectories(outDir);

System.err.println("---- " + p);
        Archive disk = new N88DiskBasicFile(p.toAbsolutePath().toString());
        for (Entry e : disk.entries()) {
            Path file = outDir.resolve(e.getName());
System.err.println(e.getName() + " -> " + file);
            Files.copy(disk.getInputStream(e), file);
        }
    }
}

/* */
