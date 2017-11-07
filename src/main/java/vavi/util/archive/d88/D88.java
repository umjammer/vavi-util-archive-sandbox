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
 * D88 形式のディスクイメージです．
 *
 * <pre>
 *  ＊ ヘッダー部
 *     トラック部(0 TRACK)
 *     トラック部(1 TRACK)
 *     ・
 *     ・
 *     ・
 *     トラック部 (83 TRACK)
 *  複数ディスクの場合、これらのファイルを連結します。
 *
 *  ＊ ヘッダー部 (サイズ 2B0H)
 *     offset    size(byte)    内容
 *     0000H    17    ディスクの名前(ASCIIZ)
 *     0011H     9    リザーブ (00H)
 *     001AH     1    ライトプロテクトフラグ (00H:なし, 10H:あり)
 *     001BH     1    ディスクの種類 (00H: 2D, 10H: 2DD, 20H: 2HD)
 *     001CH     4    (DWORD) ディスクのサイズ
 *     0020H     4    (DWORD) * 164  トラックデータテーブル (0-163 tracks)
 *
 *     ＊ トラック部 (サイズ:可変)
 *     セクター部を必要数連結したもの
 *
 *  ＊ セクター部(サイズ:可変)
 *     offset    size(byte)    内容
 *     0000H    1    ID の C
 *     0001H    1    ID の H
 *     0002H    1    ID の R
 *     0003H    1    ID の N
 *     0004H    2    (WORD) このトラックに存在するセクターの数
 *     0006H    1    記録密度 (00H: 倍密度, 40H: 単密度)
 *     0007H    1    DELETED DATA (00H:ノーマル 10H:DELETED DATA)
 *     0008H    1    ステータス (00H:ノーマルエンド,
 *                         その他エラー[DISK BIOSが返すステータス])
 *     0009H    5    リザーブ (00H)
 *     000EH    2    (WORD) セクターのサイズ
 *     0010H    可変    (000EH)で示したサイズ分のデータ
 * </pre>
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
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

            @SuppressWarnings("resource")
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

            @SuppressWarnings("resource")
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
