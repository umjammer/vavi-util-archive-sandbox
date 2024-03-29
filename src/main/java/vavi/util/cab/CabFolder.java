/*
 * You may modify, copy, and redistribute this code under the terms of
 * the GNU Library Public License version 2.1, with the exception of
 * the portion of clause 6a after the semicolon (aka the "obnoxious
 * relink clause")
 */

package vavi.util.cab;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vavi.io.LittleEndianDataInputStream;


/**
 * Encapsulates a CFFOLDER entry.
 *
 * @author Adam Megacz <adam@ibex.org>
 */
public class CabFolder {
    /** */
    public static final int COMPRESSION_NONE = 0;
    /** */
    public static final int COMPRESSION_MSZIP = 1;
    /** */
    public static final int COMPRESSION_QUANTUM = 2;
    /** */
    public static final int COMPRESSION_LZX = 3;
    /** offset of first data block within this folder */
    @SuppressWarnings("unused")
    private int firstBlockOffset = 0;
    /** number of data blocks */
    private int numBlocks = 0;
    /** compression type for this folder */
    private int compressionType = 0;
    /** per-folder reserved area */
    private byte[] reservedArea = null;
    /** */
    private List<CabFile> files = new ArrayList<>();
    /** */
    private int reservedSize;

    /** */
    public void addFile(CabFile file) {
        files.add(file);
    }

    /**
     * @return Returns the files.
     */
    public List<CabFile> getFiles() {
        return files;
    }

    /** */
    public CabFolder(int reservedSize) {
        this.reservedSize = reservedSize;
    }

    /** */
    public String toString() {
        return "[ CAB CabFolder, " + numBlocks +
               " data blocks, compression type " +
               compressionName(compressionType) + ", " +
               reservedArea.length + " bytes of reserved data ]";
    }

    /** */
    public void read(InputStream is) throws IOException {

        LittleEndianDataInputStream ledis = new LittleEndianDataInputStream(is);

        firstBlockOffset = ledis.readInt();
        numBlocks = ledis.readShort();
        compressionType = ledis.readShort() & 0x000f;
        if (compressionType != COMPRESSION_MSZIP) {
            throw new IOException("unsupported compression type: " + compressionType);
        }
        reservedArea = new byte[reservedSize];
        if (reservedArea.length > 0) {
            ledis.readFully(reservedArea);
        }
    }

    /** */
    private static String compressionName(int type) {
        switch (type) {
        case COMPRESSION_NONE:
            return "NONE";
        case COMPRESSION_MSZIP:
            return "MSZIP";
        case COMPRESSION_QUANTUM:
            return "QUANTUM";
        case COMPRESSION_LZX:
            return "LZX";
        default:
            return "<Unknown type " + type + ">";
        }
    }
}

/* */
