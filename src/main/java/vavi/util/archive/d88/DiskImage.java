/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.d88;

import java.io.IOException;
import java.io.InputStream;


/**
 * �f�B�X�N�C���[�W�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010819 nsano initial version <br>
 */
public interface DiskImage {

    /** */
    byte[] readData(int track, int surface, int sector);

    /** */
    static class Factory {
        /**
         * TODO header ��ǂ�ŃC���[�W�`����U�蕪����
         */
        public static DiskImage readFrom(InputStream is) throws IOException {
            return D88.readFrom(is);
        }
    }
}

/* */
