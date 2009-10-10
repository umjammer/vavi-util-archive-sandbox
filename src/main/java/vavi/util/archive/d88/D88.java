/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import vavi.io.LittleEndianDataInputStream;


/**
 * D88 �`���̃f�B�X�N�C���[�W�ł��D
 * 
 * <pre>
 *  �� �w�b�_�[��
 * 	�g���b�N��(0 TRACK)
 * 	�g���b�N��(1 TRACK)
 * 	�E
 * 	�E
 * 	�E
 * 	�g���b�N�� (83 TRACK)
 *  �����f�B�X�N�̏ꍇ�A�����̃t�@�C����A�����܂��B 
 * 
 *  �� �w�b�_�[�� (�T�C�Y 2B0H)
 * 	offset	size(byte)	���e
 * 	0000H	17	�f�B�X�N�̖��O(ASCIIZ)
 * 	0011H	 9	���U�[�u (00H)
 * 	001AH	 1	���C�g�v���e�N�g�t���O (00H:�Ȃ�, 10H:����)
 * 	001BH	 1	�f�B�X�N�̎�� (00H: 2D, 10H: 2DD, 20H: 2HD)
 * 	001CH	 4	(DWORD) �f�B�X�N�̃T�C�Y
 * 	0020H	 4	(DWORD) * 164  �g���b�N�f�[�^�e�[�u�� (0-163 tracks)
 * 
 * 	�� �g���b�N�� (�T�C�Y:��)
 * 	�Z�N�^�[����K�v���A����������
 *  
 *  �� �Z�N�^�[��(�T�C�Y:��)
 * 	offset	size(byte)	���e
 * 	0000H	1	ID �� C
 * 	0001H	1	ID �� H
 * 	0002H	1	ID �� R
 * 	0003H	1	ID �� N
 * 	0004H	2	(WORD) ���̃g���b�N�ɑ��݂���Z�N�^�[�̐�
 * 	0006H	1	�L�^���x (00H: �{���x, 40H: �P���x)
 * 	0007H	1	DELETED DATA (00H:�m�[�}�� 10H:DELETED DATA)
 * 	0008H	1	�X�e�[�^�X (00H:�m�[�}���G���h,
 * 			            ���̑��G���[[DISK BIOS���Ԃ��X�e�[�^�X])
 * 	0009H	5	���U�[�u (00H)
 * 	000EH	2	(WORD) �Z�N�^�[�̃T�C�Y
 * 	0010H	��	(000EH)�Ŏ������T�C�Y���̃f�[�^
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
    private static class Header {
        String name;

        int[] reserved = new int[9];

        boolean isProtected;

        int type;

        final int _2D = 0x00;

        final int _2DD = 0x10;

        final int _2HD = 0x20;

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
    private static class Track {
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

            sector.C = ledis.read();
            sector.H = ledis.read();
            sector.R = ledis.read();
            sector.N = ledis.read();
            sector.number = ledis.readShort();
            sector.density = ledis.read();
            sector.isDeleted = ledis.read() == 0x10;
            sector.status = ledis.read();
            sector.reserved[0] = ledis.read();
            sector.reserved[1] = ledis.read();
            sector.reserved[2] = ledis.read();
            sector.reserved[3] = ledis.read();
            sector.reserved[4] = ledis.read();
            sector.size = ledis.readShort();

            sector.data = new byte[sector.size];

            int l = 0;
            while (l < sector.size) {
                l += ledis.read(sector.data, l, sector.size - l);
            }

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
//                d88.tracks[i] = Track.readFrom(in);
            }
        }

        return d88;
    }

    /** */
    public byte[] readData(int track, int surface, int sector) {
        return tracks[track * 2 + surface].getSector(sector).data;
    }

    /** */
    public static void main(String[] args) throws Exception {

        InputStream is = new BufferedInputStream(new FileInputStream(args[0]));

        D88 d88 = D88.readFrom(is);
        d88.header.print();
        for (int i = 0; i < 164; i++) {
            if (d88.tracks[i] != null) {
                d88.tracks[i].print();
            }
        }
    }
}

/* */
