/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import vavi.io.LittleEndianDataInputStream;
import vavi.util.Debug;


/**
 * Represents D88 formatted disc image.
 *
 * <pre>
 *  * disk image 1
 *     header part
 *     track part
 *     :
 *     sector part start
 *     :
 *     sector part end
 *    disk image 2
 *     :
 *     :
 *
 *  multiple disk is connected disc images.
 *
 *  * header part (size 2B0H)
 *     offset    size(byte)    content
 *     0000H        17       disk name (ASCIIZ)
 *     0011H         9       reserved (00H)
 *     001AH         1       write protect flag (00H:none, 10H:protected)
 *     001BH         1       disk type (00H: 2D, 10H: 2DD, 20H: 2HD)
 *     001CH         4       (DWORD) size of disk
 *     0020H         4       (DWORD) * number of tracks
 *                   :
 *
 *  * track part (size:variable)
 *     size:
 *      first track is 672 then number of tracks is 160
 *      first track is 688 then number of tracks is 164
 *
 *  * sector part (size:variable)
 *     offset    size(byte)    content
 *     0000H        1        C ID
 *     0001H        1        H ID
 *     0002H        1        R ID
 *     0003H        1        N ID
 *     0004H        2        (WORD) sectors in this track
 *     0006H        1        density (00H: double, 40H: single)
 *     0007H        1        DELETED DATA (00H:normal 10H:DELETED DATA)
 *     0008H        1        status (00H:normal end,
 *                                   others:error[status returned by DISK BIOS])
 *     0009H        5        reserved (00H)
 *     000EH        2        (WORD) size of sector
 *     0010H        variable data, size is indicated (000EH)
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 010819 nsano initial version <br>
 * @see "https://www.pc98.org/project/doc/d88.html"
 */
public class D88 implements DiskImage {

    /** */
    private Header header;

    /** */
    private Track[] tracks = new Track[164];

    /** */
    public static class Header {
        String name;

        int[] reserved = new int[9];

        boolean isProtected;

        int type;

        static final int _2D = 0x00;

        static final int _2DD = 0x10;

        static final int _2HD = 0x20;

        int size;

        int[] tracks = new int[164];

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pr = new PrintWriter(sw);
            pr.println("name: " + name);
            for (int i = 0; i < 9; i++) {
                pr.println("reserved" + i + ": " + reserved[i]);
            }
            pr.println("isProtected: " + isProtected);
            switch (type) {
            case _2D:
                pr.println("type: 2D");
                break;
            case _2DD:
                pr.println("type: 2DD");
                break;
            case _2HD:
                pr.println("type: 2HD");
                break;
            default:
                pr.println("type: unknown: " + type);
                break;
            }
            pr.println("size: " + size);
            for (int i = 0; i < 164; i++) {
                pr.println("track" + i + ": " + tracks[i]);
            }
            return sw.toString();
        }

        /** */
        public static Header readFrom(InputStream in) throws IOException {

            Header header = new Header();

            LittleEndianDataInputStream ledis = new LittleEndianDataInputStream(in);

            byte[] buf = new byte[17];
            ledis.readFully(buf, 0, 17);
            header.name = new String(buf, 0, 16, Charset.forName("MS932"));
            for (int i = 0; i < 9; i++) {
                header.reserved[i] = ledis.read();
            }
            header.isProtected = ledis.read() == 0x10;
            header.type = ledis.read();
            header.size = ledis.readInt();

            for (int i = 0; i < 164; i++) {
                header.tracks[i] = ledis.readInt();
if (i == 0) {
 Debug.println("track[0]: " + header.tracks[i]);
}
            }

            return header;
        }
    }

    /** */
    public static class Track {
        Sector[] sectors;

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pr = new PrintWriter(sw);
            for (int i = 1; i < sectors.length; i++) {
                pr.println(sectors[i]);
            }
            return sw.toString();
        }

        public Sector getSector(int number) {
            for (Sector sector : sectors) {
                if (sector.R == number) {
                    return sector;
                }
            }
            return null;
        }

        public static Track readFrom(InputStream in) throws IOException {

            Track track = new Track();

            Sector sector = Sector.readFrom(in);

            track.sectors = new Sector[sector.number];
            track.sectors[0] = sector;

            for (int i = 1; i < sector.number; i++) {
                track.sectors[i] = Sector.readFrom(in);
            }

            return track;
        }
    }

    /** */
    private static class Sector {
        int C;
        int H;
        int R;
        int N;
        int number;
        int density;
        final int _2DD = 0x40;
        final int _2D = 0x00;
        boolean isDeleted;
        int status;
        int[] reserved = new int[5];
        int size;
        byte[] data;

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pr = new PrintWriter(sw);
            pr.println("C: " + C);
            pr.println("H: " + H);
            pr.println("R: " + R);
            pr.println("N: " + N);
            pr.println("number: " + number);
            switch (density) {
            case _2D:
                pr.println("density: 2D");
                break;
            case _2DD:
                pr.println("density: 2DD");
                break;
            default:
                pr.println("density: unknown: " + density);
                break;
            }
            pr.println("isDeleted: " + isDeleted);
            pr.println("status: " + status);
            for (int i = 0; i < 5; i++) {
                pr.println("reserved" + i + ": " + reserved[i]);
            }
            pr.println("size: " + size);
            return sw.toString();
        }

        /** */
        public static Sector readFrom(InputStream in) throws IOException {

            Sector sector = new Sector();

            LittleEndianDataInputStream ledis = new LittleEndianDataInputStream(in);

            sector.C = ledis.readUnsignedByte();
            sector.H = ledis.readUnsignedByte();
            sector.R = ledis.readUnsignedByte();
            sector.N = ledis.readUnsignedByte();
            sector.number = ledis.readUnsignedShort();
            sector.density = ledis.readByte();
            sector.isDeleted = ledis.readByte() == 0x10;
            sector.status = ledis.readByte();
            sector.reserved[0] = ledis.readByte();
            sector.reserved[1] = ledis.readByte();
            sector.reserved[2] = ledis.readByte();
            sector.reserved[3] = ledis.readByte();
            sector.reserved[4] = ledis.readByte();
            sector.size = ledis.readUnsignedShort();

            sector.data = new byte[sector.size];

            ledis.readFully(sector.data, 0, sector.size);

            return sector;
        }
    }

    /** */
    public static D88 readFrom(InputStream in) throws IOException {

        D88 d88 = new D88();

        d88.header = Header.readFrom(in);

        for (int i = 0; i < 164; i++) {
            if (d88.header.tracks[i] != 0) {
//                long l = 0; // TODO
//                while (l < d88.header.tracks[i]) {
//                    l += in.skip(d88.header.tracks[i] - l);
//                }
                d88.tracks[i] = Track.readFrom(in);
            }
        }

        return d88;
    }

    /* */
    public byte[] readData(int track, int surface, int sector) {
        return tracks[track * 2 + surface].getSector(sector).data;
    }

    /* */
    public Density getDensity() {
        switch (header.type) {
        case Header._2D:
            return Density._2D;
        case Header._2DD:
            return Density._2DD;
        case Header._2HD:
            return Density._2HD;
        default:
            return Density.UNKNOWN;
        }
    }

    /**
     * @return the header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @return the tracks
     */
    public Track[] getTracks() {
        return tracks;
    }
}

/* */
