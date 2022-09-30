/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import net.sf.sevenzipjbinding.SevenZip;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.apache.ApacheCommonsArchiveSpi;
import vavi.util.archive.arj.ApacheArjArchive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * A service provider for 7z archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/10/07 umjammer initial version <br>
 */
public class JBinding7ZipArchiveSpi extends SevenZipArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!isSupported(target)) {
            return false;
        }

        InputStream is = null;
        boolean needToClose = false;

        if (target instanceof File) {
            is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));
            needToClose = true;
        } else if (target instanceof InputStream) {
            is = (InputStream) target;
            if (!is.markSupported()) {
                throw new IllegalArgumentException("InputStream should support #mark()");
            }
        } else {
            assert false : target.getClass().getName();
        }

        // TODO accepts all
        return true;
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        if (obj instanceof File) {
            return new JBinding7ZipArchive((File) obj);
        } else if (obj instanceof InputStream) {
            return new JBinding7ZipArchive((InputStream) obj);
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class, InputStream.class};
    }

    @Override
    public String[] getFileSuffixes() {
        Set<String> suffixes = new HashSet<>();
        CompressorStreamFactory.findAvailableCompressorInputStreamProviders().forEach((name, provider) -> {
Debug.println(Level.FINER, name + ": " + StringUtil.paramString(provider));
            for (String compressorName : provider.getInputStreamCompressorNames()) {
                String[] ss = props.getProperty(compressorName).split(",", -1);
Debug.println(Level.FINER, compressorName + ": " + Arrays.toString(ss));
                suffixes.addAll(Arrays.asList(ss));
            }
        });
        return suffixes.toArray(new String[0]);
    }

    /** {compressorName: suffixes} */
    private static Properties props = new Properties();

    static {
        try {
            props.load(ApacheCommonsArchiveSpi.class.getResourceAsStream("suffixes.properties"));
        } catch (NullPointerException e) {
            throw new IllegalStateException("vavi/util/archive/sevenzip/suffixes.properties doesn't set properly");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

/* */
