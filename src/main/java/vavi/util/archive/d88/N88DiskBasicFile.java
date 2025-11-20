/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.d88.DiskImage.GeometryDiskImage;

import static java.lang.System.getLogger;


/**
 * Represents a file entry in a N88 disk basic disk.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 021123 nsano rename class <br>
 *          0.02 021123 nsano independent of D88 <br>
 */
public class N88DiskBasicFile implements Archive {

    private static final Logger logger = getLogger(N88DiskBasicFile.class.getName());

    /** */
    private static final String encoding = "MS932";

    /** */
    private final Map<String, Entry> entries = new HashMap<>();
    /** */
    private final GeometryDiskImage diskImage;
    /** */
    private final InputStream is;
    /** */
    private String name;

    /**
     * @param name filename
     */
    public N88DiskBasicFile(String name) throws IOException {
        this(new BufferedInputStream(Files.newInputStream(Paths.get(name))));
        this.name = name;
    }

    /**
     *
     */
    public N88DiskBasicFile(File file) throws IOException {
        this(new BufferedInputStream(Files.newInputStream(file.toPath())));
        this.name = file.getPath();
    }

    /**
     * {@link File#separator} in entry name will be replaced by '_'; 
     */
    public N88DiskBasicFile(InputStream is) throws IOException {

        this.is = is;
        this.diskImage = (GeometryDiskImage) DiskImage.Factory.readFrom(is);

if (logger.isLoggable(Level.DEBUG)) {
 System.err.println("-fname----:aREP   m: SC");
}
        // Directory
        //  1D(5inch)    Track 18           Sector 1 - 12
        //  2D(5inch)    Track 18 Surface 1 Sector 1 - 12
        //  2D(8inch)    Track 35 Surface 0 Sector 1 - 22
        // currently deals only 2D TODO else 2D
        int[] ts = switch (diskImage.getDensity()) {
            default -> new int[] {18, 1};
            case _2HD -> new int[] {35, 0};
        };
        int t = ts[0];
        int s = ts[1];

        for (int i = 0; i < 12; i++) {
            byte[] data = diskImage.readData(t, s, i + 1);
if (logger.isLoggable(Level.DEBUG)) {
 System.err.println(StringUtil.getDump(data));
}
            for (int j = 0; j < 16; j++) {
                switch (data[j * 16]) {
                case 0x00:
if (logger.isLoggable(Level.DEBUG)) {
 System.err.println("killed");
}
                    break;
                case (byte) 0xff:
if (logger.isLoggable(Level.DEBUG)) {
 System.err.println("not used");
}
                    break;
                default:
                    String name = new String(data, j * 16, 6, encoding) + "." +
                                  new String(data, j * 16 + 6, 3, encoding);
                    name = name.replace(File.separator, "_");
                    N88DiskBasicEntry entry = new N88DiskBasicEntry(name,
                                                                    data[j * 16 + 9],
                                                                    data[j * 16 + 10] & 0xff);
                    entries.put(name, entry);
if (logger.isLoggable(Level.DEBUG)) {
 System.err.println(entry);
}
                    break;
                }
            }
        }

        // ID Sector
        // 1D(5inch)  Track 18 Sector 13
        //  2D(5inch) Track 18 Surface 1 Sector 13
        //  2D(8inch) Track 35 Surface 0 Sector 23
        // 0x00 disk total attribute @see Entry.attribte
        // 0x01 number of files that is able to OPEN at the same time
        // 0x02 - 0xff BASIC Text
    }

    @Override
    public String getName() {
        if (name == null) {
            return is.toString();
        } else {
            return name;
        }
    }

    @Override
    public Entry[] entries() {
        Entry[] result = new Entry[entries.size()];
        Iterator<Entry> i = entries.values().iterator();
        int c = 0;
        while (i.hasNext()) {
            result[c++] = i.next(); 
        }
        return result;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public Entry getEntry(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String[] p = name.split("\\.", -1);
        String normalized = "%-6s.%-3s".formatted(p[0], p.length > 1 ? p[1] : "");
//logger.log(Level.TRACE, name + ", " + normalized);
        return entries.get(normalized);
    }

    /**
     * Cluster
     * <pre>
     *  1D (5inch)  Cluster = Track * 2 + Sector / 9
     *              Track   = Cluster / 2
     *              Sector  = (Cluster % 2) * 8 + 1 [~ 8]
     *  2D (5inch)  Cluster = Track * 4 + Surface * 2 + Sector / 9
     *              Track   = Cluster / 4
     *              Surface = (Cluster % 4) / 2
     *              Sector  = (Cluster % 2) * 8 + 1 [~ 8]
     *  2D (8inch)  Cluster = Track * 2 + Surface
     *              Track   = Cluster / 2
     *              Surface = Cluster % 2
     *              Sector  = 1 [~ 26]
     * </pre>
     * TODO currently deals only 2D
     */
    private byte[][] readCluster(int cluster) {
        int track = cluster / 4;
        int surface = (cluster % 4) / 2;
        int sector = (cluster % 2) * 8 + 1;

        byte[][] data = new byte[8][];

        for (int i = 0; i < 8; i++) {
            data[i] = diskImage.readData(track, surface, sector + i);
logger.log(Level.DEBUG, "%08x: %d, %d, %d".formatted(cluster, track, surface, (sector + i)));
        }

        return data;
    }

    @Override
    public InputStream getInputStream(Entry entry) {
        // FAT
        //  1D(5inch)    Track 18           Sector 14, 15, 16 (the three are the same)
        //  2D(5inch)    Track 18 Surface 1 Sector 14, 15, 16 (the three are the same)
        //  2D(8inch)    Track 35 Surface 0 Sector 24, 25, 26 (the three are the same)
        // TODO currently deals only 2D
        byte[] data = diskImage.readData(18, 1, 14);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int c = ((int[]) entry.getExtra())[1]; // startCluster
//logger.log(Level.TRACE, nc));

        while (true) {

            int nc = data[c] & 0xff;

            byte[][] tmp = readCluster(c);
//logger.log(Level.TRACE, "tmp: " + tmp.length + "x" + tmp[0].length);

try {
            // TODO currently deals only 2D
            int max = (nc < 0xc1) ? 8 : nc & 0x1f;
            for (int i = 0; i < max; i++) {
                os.write(tmp[i], 0, tmp[i].length);
            }
} catch (ArrayIndexOutOfBoundsException e) {
 throw new IllegalArgumentException("only support 2d (sectors 8)");
}
//logger.log(Level.TRACE, " + max + ")");

            if (nc > 0xc0) {
                break;
            }

            c = nc;
        }
//logger.log(Level.TRACE, "");

        entry.setSize(os.size());

        return new ByteArrayInputStream(os.toByteArray());
    }
}
