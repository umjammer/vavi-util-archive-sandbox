/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import vavi.io.LittleEndianDataInputStream;


/**
 * Represents D88 formatted disc image.
 *
 * <pre>
 *  * header part
 *     track part (0 TRACK)
 *     track part (1 TRACK)
 *     .
 *     .
 *     .
 *     track part (83 TRACK)
 *  multiple disk is connected those files.
 *
 *  * header part (size 2B0H)
 *     offset    size(byte)    content
 *     0000H        17       disk name (ASCIIZ)
 *     0011H         9       reserved (00H)
 *     001AH         1       write protect flag (00H:none, 10H:protected)
 *     001BH         1       disk type (00H: 2D, 10H: 2DD, 20H: 2HD)
 *     001CH         4       (DWORD) size of disk
 *     0020H         4       (DWORD) * 164  track data table (0-163 tracks)
 *
 *     * track part (size:variable)
 *     connected necessary numbers of sector parts
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

        /** */
        void print() {
            System.err.println("name: " + name);
            for (int i = 0; i < 9; i++) {
                System.err.println("reserved" + i + ": " + reserved[i]);
            }
            System.err.println("isProtected: " + isProtected);
            switch (type) {
            case _2D:
                System.err.println("type: 2D");
                break;
            case _2DD:
                System.err.println("type: 2DD");
                break;
            case _2HD:
                System.err.println("type: 2HD");
                break;
            default:
                System.err.println("type: unknown: " + type);
                break;
            }
            System.err.println("size: " + size);
            for (int i = 0; i < 164; i++) {
                System.err.println("track" + i + ": " + tracks[i]);
            }
        }

        /** */
        public static Header readFrom(InputStream in) throws IOException {

            Header header = new Header();

            LittleEndianDataInputStream ledis = new LittleEndianDataInputStream(in);

            byte[] buf = new byte[17];
            ledis.read(buf, 0, 17);
            try {
                header.name = new String(buf, 0, 16, "MS932");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(System.err);
            }
            for (int i = 0; i < 9; i++) {
                header.reserved[i] = ledis.read();
            }
            header.isProtected = ledis.read() == 0x10;
            header.type = ledis.read();
            header.size = ledis.readInt();

            for (int i = 0; i < 164; i++) {
                header.tracks[i] = ledis.readInt();
            }

            return header;
        }
    }

    /** */
    public static class Track {
        Sector[] sectors;

        void print() {
            for (int i = 1; i < sectors.length; i++) {
                sectors[i].print();
            }
        }

        public Sector getSector(int number) {
            for (int i = 0; i < sectors.length; i++) {
                if (sectors[i].R == number) {
                    return sectors[i];
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

        /** */
        void print() {
            System.err.println("C: " + C);
            System.err.println("H: " + H);
            System.err.println("R: " + R);
            System.err.println("N: " + N);
            System.err.println("number: " + number);
            switch (density) {
            case _2D:
                System.err.println("density: 2D");
                break;
            case _2DD:
                System.err.println("density: 2DD");
                break;
            default:
                System.err.println("density: unknown: " + density);
                break;
            }
            System.err.println("isDeleted: " + isDeleted);
            System.err.println("status: " + status);
            for (int i = 0; i < 5; i++) {
                System.err.println("reserved" + i + ": " + reserved[i]);
            }
            System.err.println("size: " + size);
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

            //sector.print();
            return sector;
        }
    }

    /** */
    public static D88 readFrom(InputStream in) throws IOException {

        D88 d88 = new D88();

        d88.header = Header.readFrom(in);
        //d88.header.print();

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
