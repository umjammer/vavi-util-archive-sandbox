/*
 * Copyright (c) 2002 by Naohide Sano, All Rights Reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.rar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import vavi.util.ByteUtil;
import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.win32.DateUtil;

import de.innosystec.unrar.crc.RarCRC;


/**
 * UnRar.
 *
 * TODO complete
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021023 nsano initial version <br>
 */
public class UnRar {

    private static final int NM = 260;
    private static final int SIZEOF_MARKHEAD = 7;
    private static final int SIZEOF_OLDMHD = 7;
    private static final int SIZEOF_NEWMHD = 13;
    private static final int SIZEOF_OLDLHD = 21;
    private static final int SIZEOF_NEWLHD = 32;
    private static final int SIZEOF_SHORTBLOCKHEAD = 7;
//    private static final int SIZEOF_LONGBLOCKHEAD = 11;
    private static final int SIZEOF_COMMHEAD = 13;
    private static final int SIZEOF_PROTECTHEAD = 26;
//    private static final int PACK_VER = 20;
    private static final int UNP_VER = 20;
//    private static final int PROTECT_VER = 20;
//    private static final int M_DENYREAD = 0;
//    private static final int M_DENYWRITE = 1;
//    private static final int M_DENYNONE = 2;
//    private static final int M_DENYALL = 3;
//    private static final int FILE_EMPTY = 0;
//    private static final int FILE_ADD = 1;
//    private static final int FILE_UPDATE = 2;
//    private static final int FILE_COPYOLD = 3;
//    private static final int FILE_COPYBLOCK = 4;
//    private static final int ARG_RESET = 0;
//    private static final int ARG_NEXT = 1;
//    private static final int ARG_REMOVE = 2;
    private static final int ARC = 1;
    private static final int VOL = 2;
    private static final int SFX = 3;
//    private static final int SUCCESS = 0;
    private static final int WARNING = 1;
    private static final int FATAL_ERROR = 2;
    private static final int CRC_ERROR = 3;
//    private static final int LOCK_ERROR = 4;
    private static final int WRITE_ERROR = 5;
//    private static final int OPEN_ERROR = 6;
    private static final int USER_ERROR = 7;
//    private static final int MEMORY_ERROR = 8;
    private static final int USER_BREAK = 255;
//    private static final int IMM_ABORT = 0x8000;
//    private static final int EN_LOCK = 1;
//    private static final int EN_VOL = 2;
    private static final int SD_MEMORY = 1;
    private static final int SD_FILES = 2;
    private static final int ASK_OVERWR = 0;
    private static final int ALL_OVERWR = 1;
    private static final int NO_OVERWR = 2;
//    private static final int ALARM_SOUND = 0;
//    private static final int ERROR_SOUND = 1;
    private static final int MSG_STDOUT = 0;
//    private static final int MSG_STDERR = 1;
//    private static final int MSG_NULL = 2;
    private static final int COMPARE_PATH = 0;
    private static final int NOT_COMPARE_PATH = 1;
    private static final int NAMES_DONTCHANGE = 0;
    private static final int NAMES_UPPERCASE = 1;
    private static final int NAMES_LOWERCASE = 2;
//    private static final int LOG_ARC = 1;
//    private static final int LOG_FILE = 2;
//    private static final int CRC16 = 1;
//    private static final int CRC32 = 2;
    private static final int OLD_DECODE = 0;
    private static final int OLD_ENCODE = 1;
    private static final int NEW_CRYPT = 2;
    private static final int OLD_UNPACK = 0;
    private static final int NEW_UNPACK = 1;
//    private static final int KEEP_TIME = 0;
//    private static final int LATEST_TIME = 1;
    private static final int MHD_MULT_VOL = 1;
    private static final int MHD_COMMENT = 2;
    private static final int MHD_LOCK = 4;
    private static final int MHD_SOLID = 8;
    private static final int MHD_PACK_COMMENT = 16;
    private static final int MHD_NEWNUMBERING = 16;
    private static final int MHD_AV = 32;
//    private static final int MHD_PROTECT = 64;
    private static final int LHD_SPLIT_BEFORE = 1;
    private static final int LHD_SPLIT_AFTER = 2;
    private static final int LHD_PASSWORD = 4;
    private static final int LHD_COMMENT = 8;
    private static final int LHD_SOLID = 16;
    private static final int LHD_WINDOWMASK = 0x00e0;
//    private static final int LHD_WINDOW64 = 0;
//    private static final int LHD_WINDOW128 = 32;
//    private static final int LHD_WINDOW256 = 64;
//    private static final int LHD_WINDOW512 = 96;
//    private static final int LHD_WINDOW1024 = 128;
    private static final int LHD_DIRECTORY = 0x00e0;
//    private static final int SKIP_IF_UNKNOWN = 0x4000;
    private static final int LONG_BLOCK = 0x8000;
    private static final int READSUBBLOCK = 0x8000;
    private static final int ALL_HEAD = 0;
//    private static final int MARK_HEAD = 0x72;
    private static final int MAIN_HEAD = 0x73;
    private static final int FILE_HEAD = 0x74;
    private static final int COMM_HEAD = 0x75;
//    private static final int AV_HEAD = 0x76;
    private static final int SUB_HEAD = 0x77;
    private static final int PROTECT_HEAD = 0x78;
//    private static final int EA_HEAD = 0x100;
    private static final int MS_DOS = 0;
    private static final int OS2 = 1;
    private static final int WIN_32 = 2;
    private static final int UNIX = 3;
    private static final int OLD = 1;
    private static final int NEW = 2;
    private static final int EEMPTY = -1;
//    private static final int EBREAK = 1;
    private static final int EWRITE = 2;
    private static final int EREAD = 3;
    private static final int EOPEN = 4;
    private static final int ECREAT = 5;
    private static final int ECLOSE = 6;
    private static final int ESEEK = 7;
    private static final int EMEMORY = 8;
    private static final int EARCH = 9;

    /** */
    private static class MarkHeader {
        byte[] mark = new byte[7];
    }

    /** */
    private static class OldMainHeader {
        byte[] mark = new byte[4];
        int headSize;
        byte flags;
    }

    /** */
    private static class NewMainArchiveHeader {
        int headCRC;
        byte headType;
        int flags;
        int headSize;
        int reserved;
        int reserved1;
    }

    /** */
    private static class OldFileHeader {
        int packSize;
        int unpSize;
        int fileCRC;
        int headSize;
        int fileTime;
        byte fileAttr;
        byte flags;
        byte unpVer;
        byte nameSize;
        byte method;
    }

    /** */
    private static class NewFileHeader {
        int headCRC;
        byte headType;
        int flags;
        int headSize;
        int packSize;
        int unpSize;
        byte hostOS;
        int fileCRC;
        private long fileTime;
        byte unpVer;
        byte method;
        int nameSize;
        int fileAttr;
        String name;
        void setFileTime(int value) {
            fileTime = DateUtil.dosDateTimeToLong((value & 0xff00) >> 8, value & 0xff);            
        }
        String convertDate() {
            DateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
//Debug.println("fileTime: " + fileTime);
            return format.format(new Date());
        }
        String showAttr() {
            int a;
            a = fileAttr;
            switch (hostOS) {
            case MS_DOS:
            case OS2:
            case WIN_32:
                return "  " + (((a & 0x08) != 0) ? 'V' : '.') +
                        (((a & 0x10) != 0) ? 'D' : '.') +
                        (((a & 0x01) != 0) ? 'R' : '.') +
                        (((a & 0x02) != 0) ? 'H' : '.') +
                        (((a & 0x04) != 0) ? 'S' : '.') +
                        (((a & 0x20) != 0) ? 'A' : '.') + "  ";
            default:
            case UNIX:
                return "" + (((a & 0x4000) != 0) ? 'd' : '-') +
                        (((a & 0x0100) != 0) ? 'r' : '-') +
                        (((a & 0x0080) != 0) ? 'w' : '-') +
                        (((a & 0x0040) != 0) ? (((a & 0x0800) != 0) ? 's' : 'x')
                                             : (((a & 0x0800) != 0) ? 'S' : '-')) +
                        (((a & 0x0020) != 0) ? 'r' : '-') +
                        (((a & 0x0010) != 0) ? 'w' : '-') +
                        (((a & 0x0008) != 0) ? (((a & 0x0400) != 0) ? 's' : 'x')
                                             : (((a & 0x0400) != 0) ? 'S' : '-')) +
                        (((a & 0x0004) != 0) ? 'r' : '-') +
                        (((a & 0x0002) != 0) ? 'w' : '-') +
                        (((a & 0x0001) != 0) ? 'x' : '-');
            }
        }
    }

    /** */
    private static class BlockHeader {
        int headCRC;
        byte headType;
        int flags;
        int headSize;
        int dataSize;
    }

    /** */
    private static class SubBlockHeader {
        int headCRC;
        byte headType;
        int flags;
        int headSize;
        int dataSize;
        int subType;
        byte level;
    }

    /** */
    private static class CommentHeader {
        int headCRC;
        byte headType;
        int flags;
        int headSize;
        int unpSize;
        byte unpVer;
        byte method;
        int commCRC;
    }

    /** */
    private static class ProtectHeader {
        int headCRC;
        byte headType;
        int flags;
        int headSize;
        long dataSize;
        byte version;
        int recSectors;
        long totalBlocks;
        byte[] mark = new byte[8];
    }

    /** */
    private static class RAROptions {
        int overwrite;
        int sound;
        int msgStream;
        int disableComment;
        int freshFiles;
        int updateFiles;
        int recurse;
        int packVolume;
        int allYes;
        int convertNames;
        int keepBroken;
    }

    /** */
    static class FileStat {
        long fileAttr;
        long fileTime;
        long fileSize;
        int isDir;
    }

    private MarkHeader markHead = new MarkHeader();
    private OldMainHeader oldMhd = new OldMainHeader();
    private NewMainArchiveHeader newMhd = new NewMainArchiveHeader();
    private OldFileHeader oldLhd = new OldFileHeader();
    private NewFileHeader newLhd = new NewFileHeader();
    private BlockHeader blockHead = new BlockHeader();
    private CommentHeader commHead = new CommentHeader();
    private ProtectHeader protectHead = new ProtectHeader();

    /** */
    private RAROptions opt = new RAROptions() {
        {
            overwrite = ASK_OVERWR;
            sound = 0;
            msgStream = MSG_STDOUT;
            disableComment = 0;
            freshFiles = 0;
            updateFiles = 0;
            recurse = 0;
            packVolume = 0;
            allYes = 0;
            convertNames = NAMES_DONTCHANGE;
            keepBroken = 0;
        }
    };

    private byte[] tempMemory;
    private byte[] commMemory;
    private byte[] unpMemory;
//    private byte[] argBuf;
//    private byte[] exclPtr;
//    private String argName;
    private int numArcDrive;
    private String curExtrFile;
//    private String tmpArc;
    private int solidType;
    private int lockedType;
    private int aVType;
    private int mainComment;
    private int choice;
    private String findPath;
    private String findName;
    private String extrPath;
    private RandomAccessFile arcPtr;
    private InputStream tmpArcPtr;
    private InputStream fileInPtr;
    private OutputStream fileOutPtr;
    private int unpVolume;
    private int overwriteAll = 0;
    private int arcType;
    private int sFXLen;
    private int skipUnpCRC = 0;
    private String arcName;
    private char mainCommand;
    private String password;
    private int brokenMhd;
    private int brokenFileHeader;
//    private int exclCount;
    private int arcCount;
    private int totalArcCount;
    private long arcNamesSize;
    private int testMode;
    private int exitCode = 0;
    private int checkWriteSize = 1;
    private int mainHeadSize;
    private long curBlockPos;
    private long nextBlockPos;
    private long curUnpRead;
    private long curUnpWrite;
    private int repack = 0;
    private RandomAccessFile rdUnpPtr;
    private OutputStream wrUnpPtr;
    private long unpPackedSize;
    private long destUnpSize;
//    private long latestTime;
//    private int packFileCRC;
    private int unpFileCRC;
    private int packedCRC;
    private int headerCRC;
    private int encryption;
    private int arcFormat;
//    private int packSolid;
    private int unpSolid;
    private int unpWrSize;
    private byte[] unpWrAddr;

    /** */
    private static ResourceBundle rb = ResourceBundle.getBundle("vavi.util.rar.resources",
                                                        Locale.getDefault());

    /** */
    private static final int UNP_MEMORY = 0x8000;

    /**
     * The program entry point.
     */
    public static void main(String[] args) throws Exception {
        UnRar app = new UnRar(args);
        System.exit(app.exitCode);
    }

    /** */
    public UnRar(String[] args) throws ParseException, IOException {
        executeCommand(args);
        shutDown(SD_MEMORY);
    }

    /** */
    private void errExit(int errCode, int code) throws IOException {
        String errMsg = null;
        switch (errCode) {
        case EEMPTY:
            errMsg = "";
            break;
        case EWRITE:
            errMsg = rb.getString("message.ErrWrite");
            break;
        case EREAD:
            errMsg = rb.getString("message.ErrRead");
            break;
        case ESEEK:
            errMsg = rb.getString("message.ErrSeek");
            break;
        case EOPEN:
            errMsg = rb.getString("message.ErrFOpen");
            break;
        case ECREAT:
            errMsg = rb.getString("message.ErrFCreat");
            break;
        case ECLOSE:
            errMsg = rb.getString("message.ErrFClose");
            break;
        case EMEMORY:
            errMsg = rb.getString("message.ErrOutMem");
            break;
        case EARCH:
            errMsg = rb.getString("message.ErrBrokenArc");
            break;
        }
        if (EEMPTY != errCode) {
            Debug.println("\n" + errMsg + "\n" + rb.getString("message.ProgAborted"));
        }
        shutDown(SD_FILES | SD_MEMORY);
        System.exit(code);
    }

    /** */
    void nextVolumeName(boolean newNumbering) {
        int chPtr;
        if ((chPtr = arcName.indexOf('.')) == -1) {
            arcName += ".rar";
            chPtr = arcName.indexOf('.');
        } else {
            if ((arcName.charAt(1) == 0) ||
                (arcName.indexOf("exe", chPtr + 1) == -1) ||
                (arcName.indexOf("sfx", chPtr + 1) == 0)) {
                arcName += "rar";
            }
        }

        if (newNumbering) {
        } else {
        }
    }

    /** */
    private int mergeArchive(int showFileName) throws IOException {
        arcPtr.close();
        if ((mainCommand == 'X') || (mainCommand == 'E') || (mainCommand == 'T')) {
            if ((newLhd.unpVer >= 20) && (newLhd.fileCRC != 0xffffffff) && (packedCRC != ~newLhd.fileCRC)) {
                Debug.printf(rb.getString("message.DataBadCRC"), newLhd.name, arcName);
            }
        }
        nextVolumeName(((newMhd.flags & MHD_NEWNUMBERING) != 0) &&
                       (arcFormat != OLD));
        arcPtr = new RandomAccessFile(arcName, "r");
        do {
            arcPtr = new RandomAccessFile(arcName, "r");
            if (numArcDrive == -1) {
                Debug.printf(rb.getString("message.AbsNextVol"), arcName);
                return 0;
            }
        } while(askNextVol());
        if (isArchive() == 0) {
            Debug.printf(rb.getString("message.BadArc"), arcName);
            return 0;
        }
        if (mainCommand == 'T') {
            Debug.printf(rb.getString("message.TestVol"), arcName);
        } else {
            if ((mainCommand == 'X') ||
                (mainCommand == 'E')) {
                Debug.printf(rb.getString("message.ExtrVol"), arcName);
            }
        }
        arcPtr.seek(newMhd.headSize - mainHeadSize);
        readBlock(FILE_HEAD);
//        convertFlags();
        if (showFileName != 0) {
            Debug.printf(rb.getString("message.ExtrPoints"), newLhd.name);
        }
        unpVolume = (newLhd.flags & LHD_SPLIT_AFTER);
        arcPtr.seek(nextBlockPos - newLhd.packSize);
        unpPackedSize = newLhd.packSize;
        rdUnpPtr = arcPtr;
        packedCRC = 0xffffffff;
        return 1;
    }

    /** */
    private void unstoreFile() throws IOException {
        int code;
        tempMemory = new byte[0x8000];
        while (true) {
            if ((code = unpRead(tempMemory, 0, 0x8000)) == -1) {
                errExit(EWRITE, WRITE_ERROR);
            }
            if (code == 0) {
                break;
            }
            code = (int) Math.min(code, destUnpSize);
            unpWrite(tempMemory, 0, code);
            if (destUnpSize >= 0) {
                destUnpSize -= code;
            }
        }
    }

    /** */
    private void convertPath(byte[] outPath, byte[] inPath) {
        byte[] tmpStr = new byte[NM];
        int outPathPtr;

        if ((inPath[0] != 0) && isDriveDiv(inPath[1])) {
            outPathPtr = 2;
        } else {
            outPathPtr = 0;
        }

        if ((inPath[outPathPtr] == '.') &&
            (inPath[outPathPtr + 1] == File.separatorChar)) {
            outPathPtr++;
        }
        if ((inPath[outPathPtr] == '.') && (inPath[outPathPtr + 1] == '.') &&
            (inPath[outPathPtr + 2] == File.separatorChar)) {
            outPathPtr += 2;
        }
        if (inPath[outPathPtr] == ':') {
            outPathPtr++;
        }

        System.arraycopy(tmpStr, 0, inPath, outPathPtr,
                         inPath.length - outPathPtr);
        System.arraycopy(outPath, 0, tmpStr, 0, tmpStr.length);
    }

    /** */
    private boolean askNextVol() throws IOException {
        System.out.printf(rb.getString("message.AskNextVol"), arcName);
        ask(rb.getString("message.ContinueQuit"));
        if (choice == 2) {
            errExit(EEMPTY, USER_BREAK);
        }
        return choice == 1;
    }

/** */
    private int isProcessFile(int comparePath) {
        boolean wildcards;
//        String pathArg = null;
//        String nameArg = null;
//        String pathArc = null;
//        String nameArc = null;
//        for (String argName : nextArgName) {
//            wildcards = splitPath(argName, pathArg, nameArg, 1);
//            splitPath(newLhd.name, pathArc, nameArc, 1);
//            if (nameArg.equals(nameArc)) {
//                if (((comparePath == NOT_COMPARE_PATH) &&
//                    (pathArg.charAt(0) == 0)) || pathArg.equals(pathArc) ||
//                    (wildcards && pathArc.startsWith(pathArg))) {
//                    return 1;
//                }
//            }
//        }
//        return 0;
        return 1;
    }

    /** */
    private int readBlock(int blockType) throws IOException {
        NewFileHeader saveFileHead;
        int size;
        int readSubBlock = 0;
        int lastBlock = 0;
        brokenFileHeader = 0;
        saveFileHead = newLhd;
        if ((blockType & READSUBBLOCK) != 0) {
            readSubBlock = 1;
        }
        blockType &= 0xff;
        if (arcFormat == OLD) {
            curBlockPos = arcPtr.getFilePointer();
            size = readHeader(FILE_HEAD);
            if (size != 0) {
                if ((oldLhd.method > SIZEOF_SHORTBLOCKHEAD) ||
                    (oldLhd.nameSize == 0) || (oldLhd.nameSize > 80) ||
                    (oldLhd.unpVer == 0) || (oldLhd.unpVer > 20) ||
                    (oldLhd.headSize <= 21) ||
                    ((oldLhd.flags < 8) &&
                    (oldLhd.headSize != (21 + oldLhd.nameSize)))) {
Debug.println(Level.WARNING, "here");
                    return 0;
                }
            }
            newLhd.headType = FILE_HEAD;
            newLhd.headSize = oldLhd.headSize;
            newLhd.flags = oldLhd.flags | LONG_BLOCK;
            newLhd.packSize = oldLhd.packSize;
            newLhd.unpSize = oldLhd.unpSize;
            newLhd.fileTime = oldLhd.fileTime;
            newLhd.unpVer = (oldLhd.unpVer == 2) ? (byte) 0x0d : (byte) 0x0a;
            newLhd.method = (byte) (oldLhd.method + 0x30);
            newLhd.nameSize = oldLhd.nameSize;
            newLhd.fileAttr = oldLhd.fileAttr;
            newLhd.fileCRC = oldLhd.fileCRC;
            if (size != 0) {
                nextBlockPos = curBlockPos + oldLhd.headSize + oldLhd.packSize;
            }
        } else {
            while (true) {
                curBlockPos = arcPtr.getFilePointer();
                size = readHeader(FILE_HEAD);
                if (size != 0) {
                    if (newLhd.headSize < SIZEOF_SHORTBLOCKHEAD) {
if (newLhd.headSize != 0) {
 Debug.println(Level.WARNING, "here: " + newLhd.headSize);
}
                        return 0;
                    }
                    nextBlockPos = curBlockPos + newLhd.headSize;
                    if ((newLhd.flags & LONG_BLOCK) != 0) {
                        nextBlockPos += newLhd.packSize;
                    }
                    if (nextBlockPos <= curBlockPos) {
Debug.println(Level.WARNING, "here: " + nextBlockPos + ", " + curBlockPos);
                        return 0;
                    }
                } else {
                    if (arcPtr.length() < nextBlockPos) {
                        Debug.printf("\n%s", rb.getString("message.LogUnexpEOF"));
                    }
                }
                if ((size > 0) && (blockType != SUB_HEAD)) {
                    lastBlock = blockType;
                }
                if ((size == 0) || (blockType == ALL_HEAD) ||
                    (newLhd.headType == blockType) ||
                    ((newLhd.headType == SUB_HEAD) && (readSubBlock != 0) &&
                    (lastBlock == blockType))) {
                    break;
                }
                arcPtr.seek(nextBlockPos);
            }
        }

        blockHead.headCRC = newLhd.headCRC;
        blockHead.headType = newLhd.headType;
        blockHead.flags = newLhd.flags;
        blockHead.headSize = newLhd.headSize;
        blockHead.dataSize = newLhd.packSize;

        if (blockType != newLhd.headType) {
            blockType = ALL_HEAD;
        }
        switch (blockType) {
        case FILE_HEAD:
            if (size > 0) {
                newLhd.nameSize = newLhd.nameSize;
                byte[] bytes = new byte[newLhd.nameSize];
                arcPtr.read(bytes, 0, newLhd.nameSize);
                int crc = RarCRC.checkCrc(headerCRC, bytes, 0, bytes.length);
                newLhd.name = new String(bytes);
                if ((arcFormat == NEW) &&
                    ((newLhd.headCRC & 0xffff) != (~crc & 0xffff))) {
                    brokenFileHeader = 1;
                    Debug.printf("\n%s - %s: %08x, %08x\n", newLhd.name, rb.getString("message.LogFileHead"), newLhd.headCRC, ~crc);
                }
//Debug.println("newLhd.name: " + newLhd.name);
                if (opt.convertNames == NAMES_UPPERCASE) {
                    newLhd.name = newLhd.name.toUpperCase();
                }
                if (opt.convertNames == NAMES_LOWERCASE) {
                    newLhd.name = newLhd.name.toLowerCase();
                }
                size += newLhd.nameSize;
                convertUnknownHeader();
            }
            break;
        default:
            newLhd = saveFileHead;
            arcPtr.seek(curBlockPos);
Debug.println("skip not header");
            break;
        }
        return size;
    }

    private int readHeader(int blockType) throws IOException {
        int size = 0;
        byte[] header = new byte[64];
        switch (blockType) {
        case MAIN_HEAD:
            if (arcFormat == OLD) {
                size = arcPtr.read(header, 0, SIZEOF_OLDMHD);
                System.arraycopy(oldMhd.mark, 0, header, 0, 4);
                oldMhd.headSize = ByteUtil.readLeShort(header, 4);
                oldMhd.flags = header[6];
            } else {
                size = arcPtr.read(header, 0, SIZEOF_NEWMHD);
                newMhd.headCRC = ByteUtil.readLeShort(header, 0);
                newMhd.headType = header[2];
                newMhd.flags = ByteUtil.readLeShort(header, 3);
                newMhd.headSize = ByteUtil.readLeShort(header, 5);
                newMhd.reserved = ByteUtil.readLeShort(header, 7);
                newMhd.reserved1 = ByteUtil.readLeInt(header, 9);
                headerCRC = RarCRC.checkCrc(0xffffffff, header, 2, SIZEOF_NEWMHD - 2);
            }
            break;
        case FILE_HEAD:
            if (arcFormat == OLD) {
                size = arcPtr.read(header, 0, SIZEOF_OLDLHD);
                oldLhd.packSize = ByteUtil.readLeInt(header, 0);
                oldLhd.unpSize = ByteUtil.readLeInt(header, 4);
                oldLhd.fileCRC = ByteUtil.readLeShort(header, 8);
                oldLhd.headSize = ByteUtil.readLeShort(header, 10);
                oldLhd.fileTime = ByteUtil.readLeInt(header, 12);
                oldLhd.fileAttr = header[16];
                oldLhd.flags = header[17];
                oldLhd.unpVer = header[18];
                oldLhd.nameSize = header[19];
                oldLhd.method = header[20];
            } else {
                size = arcPtr.read(header, 0, SIZEOF_NEWLHD);
                newLhd.headCRC = ByteUtil.readLeShort(header, 0);
                newLhd.headType = header[2];
                newLhd.flags = ByteUtil.readLeShort(header, 3);
                newLhd.headSize = ByteUtil.readLeShort(header, 5);
                newLhd.packSize = ByteUtil.readLeInt(header, 7);
                newLhd.unpSize = ByteUtil.readLeInt(header, 11);
                newLhd.hostOS = header[15];
                newLhd.fileCRC = ByteUtil.readLeInt(header, 16);
                newLhd.setFileTime(ByteUtil.readLeInt(header, 20));
                newLhd.unpVer = header[24];
                newLhd.method = header[25];
                newLhd.nameSize = ByteUtil.readLeShort(header, 26);
                newLhd.fileAttr = ByteUtil.readLeInt(header, 28);
                headerCRC = RarCRC.checkCrc(0xffffffff, header, 2, SIZEOF_NEWLHD - 2);
            }
            break;
        case COMM_HEAD:
            size = arcPtr.read(header, 0, SIZEOF_COMMHEAD);
            commHead.headCRC = ByteUtil.readLeShort(header, 0);
            commHead.headType = header[2];
            commHead.flags = ByteUtil.readLeShort(header, 3);
            commHead.headSize = ByteUtil.readLeShort(header, 5);
            commHead.unpSize = ByteUtil.readLeShort(header, 7);
            commHead.unpVer = header[9];
            commHead.method = header[10];
            commHead.commCRC = ByteUtil.readLeShort(header, 11);
            headerCRC = RarCRC.checkCrc(0xffffffff, header, 2, SIZEOF_COMMHEAD - 2);
            break;
        case PROTECT_HEAD:
            size = arcPtr.read(header, 0, SIZEOF_PROTECTHEAD);
            protectHead.headCRC = ByteUtil.readLeShort(header, 0);
            protectHead.headType = header[2];
            protectHead.flags = ByteUtil.readLeShort(header, 3);
            protectHead.headSize = ByteUtil.readLeShort(header, 5);
            protectHead.dataSize = ByteUtil.readLeInt(header, 7);
            protectHead.version = header[11];
            protectHead.recSectors = ByteUtil.readLeShort(header, 12);
            protectHead.totalBlocks = ByteUtil.readLeInt(header, 14);
            System.arraycopy(protectHead.mark, 0, header, 18, 8);
            headerCRC = RarCRC.checkCrc(0xffffffff, header, 2, SIZEOF_PROTECTHEAD - 2);
            break;
        case ALL_HEAD:
            size = arcPtr.read(header, 0, SIZEOF_SHORTBLOCKHEAD);
            blockHead.headCRC = ByteUtil.readLeShort(header, 0);
            blockHead.headType = header[2];
            blockHead.flags = ByteUtil.readLeShort(header, 3);
            blockHead.headSize = ByteUtil.readLeShort(header, 5);
            if ((blockHead.flags & LONG_BLOCK) != 0) {
                size += arcPtr.read(header, 7, 4);
                blockHead.dataSize = ByteUtil.readLeInt(header, 7);
            }
            break;
        }
        return size;
    }

    /** comment */
    private void viewComment() throws IOException {
        long curPos;
        int commLen;
        int unpCommLen;
        if (mainComment == 0) {
            return;
        }
        curPos = arcPtr.getFilePointer();
        if (arcFormat == OLD) {
            commLen = arcPtr.read() + (arcPtr.read() << 8);
        } else {
            if ((readHeader(COMM_HEAD) < 7) ||
                (commHead.headType != COMM_HEAD) || (commHead.headSize < 7)) {
                return;
            }
            if (commHead.headCRC != ~headerCRC) {
                Debug.printf("\n%s\n", rb.getString("message.LogCommHead"));
                arcPtr.seek(curPos);
                return;
            }
            commLen = commHead.headSize - SIZEOF_COMMHEAD;
        }
        if (opt.disableComment == 0) {
            Debug.println("");
        }
        if (((arcFormat == OLD) && ((oldMhd.flags & MHD_PACK_COMMENT) != 0)) ||
            ((arcFormat == NEW) && (commHead.method != 0x30))) {
            if ((arcFormat == NEW) &&
                ((commHead.unpVer < 15) || (commHead.unpVer > UNP_VER) ||
                (commHead.method > 0x35))) {
                arcPtr.seek(curPos);
                return;
            }
            unpMemory = new byte[UNP_MEMORY];
            testMode = 2;
            unpVolume = 0;
            if (arcFormat == OLD) {
                unpCommLen = arcPtr.read() + (arcPtr.read() << 8);
                commLen -= 2;
                char pn1 = 0;
                char pn2 = 7;
                char pn3 = 77;
                encryption = 13;
            } else {
                encryption = 0;
                unpCommLen = commHead.unpSize;
            }
            destUnpSize = unpCommLen;
            unpPackedSize = commLen;
            unpFileCRC = 0xffffffff;
            rdUnpPtr = arcPtr;
            unpSolid = 0;
            suspend = 0;
            repack = 0;
            skipUnpCRC = 0;
            tunpack(unpMemory, 0,
                    (commHead.unpVer <= 15) ? OLD_UNPACK : NEW_UNPACK);
            if ((arcFormat == NEW) && (~unpFileCRC != commHead.commCRC)) {
                Debug.printf("\n%s", rb.getString("message.LogCommBrk"));
            } else {
                showComment(unpWrAddr, unpWrSize);
            }
        } else {
            tempMemory = new byte[commLen];
            arcPtr.read(tempMemory, 0, commLen);
            int crc = RarCRC.checkCrc(0xffffffff,tempMemory, 0, commLen);
            if ((arcFormat == NEW) && (commHead.commCRC != ~crc)) {
                Debug.printf("\n%s", rb.getString("message.LogCommBrk"));
            } else {
                showComment(tempMemory, commLen);
            }
        }
        arcPtr.seek(curPos);
    }

    /** */
    private void viewFileComment() throws IOException {
        long curPos;
        int commLen;
        if (((newLhd.flags & LHD_COMMENT) == 0) || (opt.disableComment != 0)) {
            return;
        }
        commMemory = new byte[0x8000];
        System.err.println();
        curPos = arcPtr.getFilePointer();
        if (arcFormat == OLD) {
            commLen = arcPtr.read() + (arcPtr.read() << 8);
            arcPtr.read(commMemory, 0, commLen);
            System.out.write(commMemory, 0, commLen);
            System.out.flush();
        } else {
            if ((readHeader(COMM_HEAD) < 7) ||
                (commHead.headType != COMM_HEAD) || (commHead.headSize < 7)) {
                return;
            }
            if (commHead.headCRC != ~headerCRC) {
                Debug.printf("\n%s", rb.getString("message.LogCommHead"));
                arcPtr.seek(curPos);
                return;
            }
            if ((commHead.unpVer < 15) || (commHead.unpVer > UNP_VER) ||
                (commHead.method > 0x30)) {
                arcPtr.seek(curPos);
                return;
            }
            arcPtr.read(commMemory, 0, commHead.unpSize);
            int crc = RarCRC.checkCrc(0xffffffff, commMemory, 0, commHead.unpSize);
            if (commHead.commCRC != ~crc) {
                Debug.printf("\n%s", rb.getString("message.LogBrokFCmt"));
            } else {
                System.out.write(commMemory, 0, commHead.unpSize);
                System.out.flush();
            }
        }
        arcPtr.seek(curPos);
    }

    /** */
    void showComment(byte[] addr, int size) {
        if (opt.disableComment == 0) {
            switch (kbdAnsi(new String(addr, 0, size))) {
            case 1:
                showAnsiComment(new String(addr, 0, size));
                break;
            case 2:
                return;
            default:
                System.out.write(addr, 0, size);
                System.out.flush();
                break;
            }
        }
    }

    /** */
    private int kbdAnsi(String message) {
        int retCode = 0;
        int chPtr = message.length();
        while (chPtr-- > 0) {
            if ((message.charAt(chPtr) == 27) &&
                (message.charAt(chPtr + 1) == 91)) {
                retCode = 1;
                chPtr += 2;
                while (!(((message.charAt(chPtr) >= 65) &&
                       (message.charAt(chPtr) <= 90)) ||
                       ((message.charAt(chPtr) >= 97) &&
                       (message.charAt(chPtr) <= 122)))) {
                    if (message.charAt(chPtr++) == 34) {
                        return 2;
                    }
                }
                if ((message.charAt(chPtr) == 80) ||
                    (message.charAt(chPtr) == 112)) {
                    return 2;
                }
            }
        }
        return retCode;
    }

// compr

    private static final int NC = 298; // alphabet = {0, 1, 2, ..., NC - 1}
    private static final int DC = 48;
    private static final int RC = 28;
    private static final int BC = 19;
    private static final int MC = 257;
    private static final int CODE_HUFFMAN = 0;
    private static final int CODE_LZ = 1;
    private static final int CODE_LZ2 = 2;
    private static final int CODE_REPEATLZ = 3;
    private static final int CODE_CACHELZ = 4;
    private static final int CODE_STARTFILE = 5;
    private static final int CODE_ENDFILE = 6;
    private static final int CODE_STARTMM = 8;
    private static final int CODE_ENDMM = 7;
    private static final int CODE_MMDELTA = 9;

    /** */
    private static class AudioVariables {
        int k1;
        int k2;
        int k3;
        int k4;
        int k5;
        int d1;
        int d2;
        int d3;
        int d4;
        int lastDelta;
        int[] dif = new int[11];
        int byteCount;
        int lastChar;
    }

    /** */
    private int blockSymCode;
    /** */
    private int blockSymLength;
    /** */
    private byte[] packOldTable = new byte[MC * 4];

// crccrypt

    /** */
    private static final int NROUNDS = 32;

    /** */
    private int rol(long x, int n) {
        return (int) ((x << n) | (x >> (NROUNDS - n)));
    }

    /** */
    private int ror(long x, int n) {
        return (int) ((x >> n) | (x << (NROUNDS - n)));
    }

    /** */
    private int substLong(long t) {
        return substTable[(int) t & 255] |
               (substTable[(int) (t >> 8) & 255] << 8) |
               (substTable[(int) (t >> 16) & 255] << 16) |
               (substTable[(int) (t >> 24) & 255] << 24);
    }

    /** */
    private int[] crcTab = new int[256];
    /** */
    private int[] substTable = new int[256];
    /** */
    private static final int[] initSubstTable = {
        215, 19, 149, 35, 73, 197, 192, 205,
        249, 28, 16, 119, 48, 221, 2, 42,
        232, 1, 177, 233, 14, 88, 219, 25,
        223, 195, 244, 90, 87, 239, 153, 137,
        255, 199, 147, 70, 92, 66, 246, 13,
        216, 40, 62, 29, 217, 230, 86, 6, 71,
        24, 171, 196, 101, 113, 218, 123, 93,
        91, 163, 178, 202, 67, 44, 235, 107,
        250, 75, 234, 49, 167, 125, 211, 83,
        114, 157, 144, 32, 193, 143, 36, 158,
        124, 247, 187, 89, 214, 141, 47, 121,
        228, 61, 130, 213, 194, 174, 251, 97,
        110, 54, 229, 115, 57, 152, 94, 105,
        243, 212, 55, 209, 245, 63, 11, 164,
        200, 31, 156, 81, 176, 227, 21, 76,
        99, 139, 188, 127, 17, 248, 51, 207,
        120, 189, 210, 8, 226, 41, 72, 183,
        203, 135, 165, 166, 60, 98, 7, 122,
        38, 155, 170, 69, 172, 252, 238, 39,
        134, 59, 128, 236, 27, 240, 80, 131,
        3, 85, 206, 145, 79, 154, 142, 159,
        220, 201, 133, 74, 64, 20, 129, 224,
        185, 138, 103, 173, 182, 43, 34, 254,
        82, 198, 151, 231, 180, 58, 10, 118,
        26, 102, 12, 50, 132, 22, 191, 136,
        111, 162, 179, 45, 4, 148, 108, 161,
        56, 78, 126, 242, 222, 15, 175, 146,
        23, 33, 241, 181, 190, 77, 225, 0,
        46, 169, 186, 68, 95, 237, 65, 53,
        208, 253, 168, 9, 18, 100, 52, 116,
        184, 160, 96, 109, 37, 30, 106, 140,
        104, 150, 5, 204, 117, 112, 84
    };
    /** */
    long[] key = new long[4];
    /** */
    long[] oldKey = new long[4];
    /** */
    int pn1;
    /** */
    int pn2;
    /** */
    int pn3;

    /** */
    private int argsUsed = 0;

    /** */
    private void encryptBlock(byte[] buf, int p) {
        long a = buf[p + 0] ^ key[0];
        long b = buf[p + 1] ^ key[1];
        long c = buf[p + 2] ^ key[2];
        long d = buf[p + 3] ^ key[3];

        for (int i = 0; i < NROUNDS; i++) {
            long t = (c + rol(d, 11)) ^ key[i & 3];
            long ta = a ^ substLong(t);
            t = (d ^ rol(c, 17)) + key[i & 3];

            long tb = b ^ substLong(t);
            a = c;
            b = d;
            c = ta;
            d = tb;
        }

        buf[p + 0] = (byte) (c ^ key[0]);
        buf[p + 1] = (byte) (d ^ key[1]);
        buf[p + 2] = (byte) (a ^ key[2]);
        buf[p + 3] = (byte) (b ^ key[3]);

        updKeys(buf);
    }

    /** */
    private void decryptBlock(byte[] buf, int p) {
        byte[] inBuf = new byte[16];

        long a = buf[p + 0] ^ key[0];
        long b = buf[p + 1] ^ key[1];
        long c = buf[p + 2] ^ key[2];
        long d = buf[p + 3] ^ key[3];

        System.arraycopy(inBuf, 0, buf, 0, inBuf.length);
        for (int i = NROUNDS - 1; i >= 0; i--) {
            long t = (c + rol(d, 11)) ^ key[i & 3];
            long ta = a ^ substLong(t);
            t = (d ^ rol(c, 17)) + key[i & 3];

            long tb = b ^ substLong(t);
            a = c;
            b = d;
            c = ta;
            d = tb;
        }

        buf[p + 0] = (byte) (c ^ key[0]);
        buf[p + 1] = (byte) (d ^ key[1]);
        buf[p + 2] = (byte) (a ^ key[2]);
        buf[p + 3] = (byte) (b ^ key[3]);

        updKeys(inBuf);
    }

    /** */
    private void updKeys(byte[] buf) {
        for (int i = 0; i < 16; i += 4) {
            key[0] ^= crcTab[buf[i]];
            key[1] ^= crcTab[buf[i + 1]];
            key[2] ^= crcTab[buf[i + 2]];
            key[3] ^= crcTab[buf[i + 3]];
        }
    }

    /** */
    private void swap(int[] ch1, int c1, int[] ch2, int c2) {
        int ch;
        ch = ch1[c1];
        ch1[c1] = ch2[c2];
        ch2[c2] = ch;
    }

    /** */
    private  void setCryptKeys(byte[] password) {
        int i;
        int j;
        int k;
        char n1;
        char n2;

        byte[] psw = new byte[256];
        setOldKeys(password);
        key[0] = 0xd3a3b879L;
        key[1] = 0x3f6d12f7L;
        key[2] = 0x7515a235L;
        key[3] = 0xa4e7f123L;
        System.arraycopy(psw, 0, password, 0, password.length);
        System.arraycopy(substTable, 0, initSubstTable, 0, substTable.length);
        for (j = 0; j < 256; j++) {
            for (i = 0; i < psw.length; i += 2) {
                n2 = (char) crcTab[(psw[i + 1] + j) & 0xff];
                for (k = 1, n1 = (char) crcTab[(psw[i] - j) & 0xff]; n1 != n2;
                     n1++, k++) {
                    swap(substTable, n1, substTable, (n1 + i + k) & 0xff);
                }
            }
        }
        for (i = 0; i < psw.length; i += 16) {
            encryptBlock(psw, i);
        }
    }

    /** */
    private void setOldKeys(byte[] password) {
        long pswcrc;
        byte ch;
        pswcrc = RarCRC.checkCrc(0xffffffff, password, 0, password.length);
        oldKey[0] = pswcrc;
        oldKey[1] = pswcrc >> 16;
        oldKey[2] = oldKey[3] = 0;
        pn1 = pn2 = pn3 = 0;

        int p = 0;
        while ((ch = password[p]) != 0) {
            pn1 += ch;
            pn2 ^= ch;
            pn3 += ch;
            pn3 = rol(pn3, 1);
            oldKey[2] ^= (ch ^ crcTab[ch]);
            oldKey[3] += (ch + (crcTab[ch] >> 16));
            p++;
        }
    }

    /** */
    private void crypt(byte[] data, int count, int method) {
        if (method == OLD_DECODE) {
            decode13(data, count);
        } else {
            if (method == OLD_ENCODE) {
                encode13(data, count);
            } else {
                crypt15(data, count);
            }
        }
    }

    /** */
    private void encode13(byte[] data, int count) {
        int p = 0;
        while (count-- > 0) {
            pn2 += pn3;
            pn1 += pn2;
            data[p] += (byte) pn1;
            p++;
        }
    }

    /** */
    private void decode13(byte[] data, int count) {
        int p = 0;
        while (count-- > 0) {
            pn2 += pn3;
            pn1 += pn2;
            data[p] -= pn1;
            p++;
        }
    }

    /** */
    private void crypt15(byte[] data, int count) {
        int p = 0;
        while (count-- > 0) {
            oldKey[0] += 0x1234;
            oldKey[1] ^= crcTab[(int) ((oldKey[0] & 0x1fe) >> 1)];
            oldKey[2] -= (crcTab[(int) ((oldKey[0] & 0x1fe) >> 1)] >> 16);
            oldKey[0] ^= oldKey[2];
            oldKey[3] = ror(oldKey[3], 1) ^ oldKey[1];
            oldKey[3] = ror(oldKey[3], 1);
            oldKey[0] ^= oldKey[3];
            data[p] ^= (oldKey[0] >> 8);
            p++;
        }
    }

// extract

    /** */
    public void extrFile() throws IOException {
        File fs = null;
        String destFileName = null;
        int chPtr;
        long fileCount = 0;
        long totalFileCount;
        long errCount = 0;
        int skipSolid;
        int extrFile;
        int size;
        boolean allArgsUsed;
        boolean mDCode;
        int userReject = 0;
        int tmpPassword = 0;
        int brokenFile = 0;
        int firstFile;

GET_NEXT_ARCHIVE:

        while (readArcName() != 0) {
// SKIP_TO_FIRST_VOL:
            totalFileCount = 0;
            allArgsUsed = false;
            firstFile = 1;
            try {
                arcPtr = new RandomAccessFile(arcName, "r");
            } catch (IOException e) {
                continue;
            }

            if (tmpPassword != 0) {
                tmpPassword = 0;
                password = null;
            }
            if (isArchive() == 0) {
                Debug.printf(rb.getString("message.NotRAR"), arcName);
                arcPtr.close();
                continue;
            }

            if (mainCommand == 'T') {
                Debug.printf(rb.getString("message.ExtrTest"), arcName);
            } else {
                Debug.printf(rb.getString("message.Extracting"), arcName);
            }

            viewComment();

            unpMemory = new byte[UNP_MEMORY];

            arcPtr.seek(newMhd.headSize - mainHeadSize);
            unpVolume = 0;

            while (true) {
                size = readBlock(FILE_HEAD | READSUBBLOCK);
                if ((size <= 0) && (unpVolume == 0)) {
Debug.println("readBlock");
                    break;
                }
                if (blockHead.headType == SUB_HEAD) {
Debug.println("skip sub head");
                    arcPtr.seek(nextBlockPos);
                    continue;
                }

                if (allArgsUsed) {
Debug.println("allArgsUsed");
                    break;
                }
                convertPath(newLhd.name.getBytes(), newLhd.name.getBytes());

//                convertFlags();

                if (((newLhd.flags & LHD_SPLIT_BEFORE) != 0) &&
                    (solidType != 0) && (firstFile != 0)) {
                    arcPtr.close();
                    chPtr = arcName.lastIndexOf('.');
                    if ((chPtr == 0) || !arcName.endsWith(".rar")) {
                        setExt(arcName, "rar");
                        if (new File(arcName).exists()) {
                            continue;
                        }
                        setExt(arcName, "exe");
                        if (new File(arcName).exists()) {
                            continue;
                        }
                    }
                    Debug.println(rb.getString("message.NeedFirstVol"));
                    if (totalArcCount > 1) {
                        continue GET_NEXT_ARCHIVE;
                    } else {
                        errExit(EEMPTY, FATAL_ERROR);
                    }
                }
                firstFile = 0;
                if ((unpVolume != 0) && (size == 0) && (mergeArchive(0) == 0)) {
                    errCount++;
                    exitCode = WARNING;
                    continue GET_NEXT_ARCHIVE;
                }
                unpVolume = ((newLhd.flags & LHD_SPLIT_AFTER) != 0) ? 1 : 0;

                arcPtr.seek(nextBlockPos - newLhd.packSize);

                testMode = 0;
                extrFile = 0;
                skipSolid = 0;

                if (((isProcessFile(COMPARE_PATH) != 0) &&
                    ((newLhd.flags & LHD_SPLIT_BEFORE) == 0)) ||
                    ((skipSolid = solidType) != 0)) {
                    if ((newLhd.flags & LHD_PASSWORD) != 0) {
                        if (password == null) {
                            if (getPassword(1) != 1) {
                                errExit(EEMPTY, USER_BREAK);
                            }
                            tmpPassword = (solidType != 0) ? 2 : 1;
                        } else if (tmpPassword == 1) {
                            Debug.println(rb.getString("message.UseCurPsw, newLhd.name"));
                            ask(rb.getString("message.YesNoAll"));
                            switch (choice) {
                            case -1:
                                errExit(EEMPTY, USER_BREAK);
                            case 2:
                                if (getPassword(1) != 1) {
                                    errExit(EEMPTY, USER_BREAK);
                                }
                                break;
                            case 3:
                                tmpPassword = 2;
                                break;
                            }
                        }
                    }

                    destFileName = extrPath;

                    if (mainCommand == 'E') {
                        destFileName += pointToName(newLhd.name);
                    } else {
                        destFileName += newLhd.name;
                    }

                    extrFile = (skipSolid != 0) ? 0 : 1;
                    if (((opt.freshFiles != 0) || (opt.updateFiles != 0)) &&
                        ((mainCommand == 'E') ||
                        (mainCommand == 'X'))) {
                            fs = new File(destFileName);
                            if (fs.lastModified() >= newLhd.fileTime) {
                                extrFile = 0;
                            }
                            if (!fs.exists()) {
                                if (opt.freshFiles != 0) {
                                    extrFile = 0;
                                }
                            }
                    }

                    if ((newLhd.unpVer < 13) || (newLhd.unpVer > UNP_VER)) {
                        Debug.printf(rb.getString("message.UnknownMeth"), newLhd.name);
                        extrFile = 0;
                        errCount++;
                        exitCode = WARNING;
                    }

//                    if (FS.isLabel()) {
//                        continue;
//                    }
                    if (fs.isDirectory()) {
                        if ((mainCommand == 'P') ||
                            (mainCommand == 'E')) {
                            continue;
                        }
                        if (skipSolid != 0) {
                            Debug.printf(rb.getString("message.ExtrSkipDir"), newLhd.name);
                            continue;
                        }
                        fileCount++;
                        if (mainCommand == 'T') {
                            Debug.printf(rb.getString("message.ExtrTestDir"), newLhd.name);
                            continue;
                        }
                        if (!(mDCode = new File(destFileName).mkdir())) {
                            new File(destFileName).mkdirs();
                            if (!(mDCode = new File(destFileName).mkdir())) {
                                Debug.printf(rb.getString("message.ExtrErrMkDir"), newLhd.name);
                                exitCode = WARNING;
                            }
                        }
                        if (!mDCode) {
                            Debug.printf(rb.getString("message.CreatDir"), newLhd.name);
                        }
                        continue;
                    } else {
                        if ((mainCommand == 'T') && (extrFile != 0)) {
                            testMode = 1;
                        }
                        if ((mainCommand == 'P') && (extrFile != 0)) {
                            fileInPtr = System.in;
                        }
                        if (((mainCommand == 'E') ||
                            (mainCommand == 'X')) && (extrFile != 0)) {
                            try {
                            fileOutPtr = new FileOutputStream(destFileName, opt.overwrite > 0); // , userReject
                            } catch (IOException e) {
                                if (userReject == 0) {
                                    Debug.printf(rb.getString("message.CannotCreate"), destFileName);
                                    errCount++;
                                    exitCode = WARNING;
                                }
                                extrFile = 0;
                                allArgsUsed = nextArgName.length - 1 == argsUsed;
                            }
                        }
                    }

                    if ((extrFile == 0) && (solidType != 0)) {
                        skipSolid = 1;
                        testMode = 1;
                        extrFile = 1;
                    }
                    if (extrFile != 0) {
                        if (skipSolid == 0) {
                            fileCount++;
                        }
                        totalFileCount++;
                        if (skipSolid != 0) {
                            Debug.printf(rb.getString("message.ExtrSkipFile"), newLhd.name);
                        } else {
                            switch (mainCommand) {
                            case 'T':
                                Debug.printf(rb.getString("message.ExtrTestFile"), newLhd.name);
                                break;
                            case 'P':
                                Debug.printf(rb.getString("message.ExtrPrinting"), newLhd.name);
                                checkWriteSize = 0;
                                break;
                            case 'X':
                            case 'E':
                                Debug.printf(rb.getString("message.ExtrFile"), destFileName);
                                break;
                            }
                        }
                        curExtrFile = (skipSolid != 0) ? "" : destFileName;
                        curUnpRead = curUnpWrite = 0;
                        unpFileCRC = (arcFormat == OLD) ? 0 : 0xffffffff;
                        packedCRC = 0xffffffff;
                        if ((password != null) && ((newLhd.flags & LHD_PASSWORD) != 0)) {
                            encryption = newLhd.unpVer;
                        } else {
                            encryption = 0;
                        }
                        if (encryption != 0) {
                            setCryptKeys(password.getBytes());
                        }
                        unpPackedSize = newLhd.packSize;
                        destUnpSize = newLhd.unpSize;
                        rdUnpPtr = arcPtr;
                        wrUnpPtr = fileOutPtr;
                        suspend = 0;
                        repack = 0;
                        skipUnpCRC = skipSolid;
                        if (newLhd.method == 0x30) {
                            unstoreFile();
                        } else {
                            if (newLhd.unpVer <= 15) {
                                tunpack(unpMemory,
                                        ((totalFileCount > 1) &&
                                        (solidType != 0)) ? 1 : 0, OLD_UNPACK);
                            } else {
                                tunpack(unpMemory, newLhd.flags & LHD_SOLID,
                                        NEW_UNPACK);
                            }
                        }
                        if (skipSolid == 0) {
                            allArgsUsed = nextArgName.length - 1 == argsUsed;
                        }

                        if (mainCommand == 'P') {
                            checkWriteSize = 1;
                        }
                        if (arcPtr != null) {
                            arcPtr.seek(nextBlockPos);
                        }
                        if (skipSolid == 0) {
                            if (((arcFormat == OLD) &&
                                (unpFileCRC == newLhd.fileCRC)) ||
                                ((arcFormat == NEW) &&
                                (unpFileCRC == ~newLhd.fileCRC))) {
                                if (mainCommand != 'P') {
                                    Debug.println(rb.getString("message.Ok"));
                                }
                                brokenFile = 0;
                            } else {
                                if ((newLhd.flags & LHD_PASSWORD) != 0) {
                                    Debug.printf(rb.getString("message.EncrBadCRC"), newLhd.name);
                                } else {
                                    Debug.printf("\n%-20s - %s", newLhd.name, rb.getString("message.CRCFailed"));
                                }
                                exitCode = CRC_ERROR;
                                errCount++;
                                brokenFile = 1;
                            }
                        }
                        if (testMode == 1) {
                            testMode = 0;
                        } else {
                            if ((mainCommand == 'X') ||
                                (mainCommand == 'E')) {
//                                setOpenFileStat(filePtr);
                                fileInPtr.close();
//                                setCloseFileStat(destFileName);
                                if ((brokenFile != 0) && (opt.keepBroken == 0)) {
                                    new File(destFileName).delete();
                                }
                            }
                        }
                        curExtrFile = null;
                    }
                }
                if (arcPtr == null) {
                    continue GET_NEXT_ARCHIVE;
                }
                if (extrFile == 0) {
                    if (solidType == 0) {
                        arcPtr.seek(nextBlockPos);
                    } else {
                        if (skipSolid == 0) {
                            break;
                        }
                    }
                }
                if (allArgsUsed) {
                    break;
                }
            }
            arcPtr.close();
        }
        if (fileCount == 0) {
            Debug.println(rb.getString("message.ExtrNoFiles"));
            exitCode = WARNING;
        } else {
            if (errCount == 0) {
                Debug.println(rb.getString("message.ExtrAllOk"));
            } else {
                Debug.printf(rb.getString("message.ExtrTotalErr"), errCount);
            }
        }
        if (tmpPassword != 0) {
            password = null;
        }
    }

    /** list */
    public void listArchive() throws IOException {
        long totalPackSize;
        long totalUnpSize;
        long fileCount;
        long archivesCount;
        long sumPackSize;
        long sumUnpSize;
        long sumFileCount;
        int i;
        int outHeader;
        archivesCount = sumPackSize = sumUnpSize = sumFileCount = 0;
        while (readArcName() != 0) {
            arcPtr = new RandomAccessFile(arcName, "r");

            while (true) {
                totalPackSize = totalUnpSize = fileCount = 0;
                if (isArchive() != 0) {
                    outHeader = 0;
                    viewComment();
                    arcPtr.seek(newMhd.headSize - mainHeadSize);
                    System.err.println();
                    if (solidType != 0) {
                        Debug.println(rb.getString("message.ListSolid"));
                    }
                    if (sFXLen > 0) {
                        Debug.println(rb.getString("message.ListSFX"));
                    }
                    if (arcType == VOL) {
                        if (solidType != 0) {
                            Debug.println(rb.getString("message.ListVol1"));
                        } else {
                            Debug.println(rb.getString("message.ListVol2"));
                        }
                    } else {
                        if (solidType != 0) {
                            Debug.println(rb.getString("message.ListArc1"));
                        } else {
                            Debug.println(rb.getString("message.ListArc2"));
                        }
                    }
                    System.out.printf(" %s%n", arcName);
                    while (readBlock(FILE_HEAD) > 0) {
                        if (isProcessFile(NOT_COMPARE_PATH) != 0) {
                            if (outHeader == 0) {
                                if (mainCommand == 'V') {
                                    System.out.printf(rb.getString("message.ListPathComm"));
                                } else {
                                    System.out.printf(rb.getString("message.ListName"));
                                }
                                System.out.println(rb.getString("message.ListTitle"));
                                for (i = 0; i < 79; i++) {
                                    System.out.print("-");
                                }
                                outHeader = 1;
                            }

                            System.out.printf("%n%c", ((newLhd.flags & LHD_PASSWORD) != 0) ? '*' : ' ');
                            if ((mainCommand == 'V') ||
                                (pointToName(newLhd.name).length() >= 13)) {
                                System.out.printf("%s", newLhd.name);
                                viewFileComment();
                                System.out.printf("%n%12s ", "");
                            } else {
                                System.out.printf("%-12s", pointToName(newLhd.name));
                            }

                            System.out.printf(" %8d %8d ", newLhd.unpSize, newLhd.packSize);

                            if (((newLhd.flags & LHD_SPLIT_BEFORE) != 0) &&
                                ((newLhd.flags & LHD_SPLIT_AFTER) != 0)) {
                                System.out.print(" <->");
                            } else {
                                if ((newLhd.flags & LHD_SPLIT_BEFORE) != 0) {
                                    System.out.print(" <--");
                                } else {
                                    if ((newLhd.flags & LHD_SPLIT_AFTER) != 0) {
                                        System.out.print(" -->");
                                    } else {
                                        System.out.printf("%3d%%", toPercent(newLhd.packSize, newLhd.unpSize));
                                    }
                                }
                            }

                            System.out.printf(" %s ", newLhd.convertDate());

                            System.out.print(newLhd.showAttr());

                            System.out.printf(" %16X", newLhd.fileCRC);
                            System.out.printf(" m%d", newLhd.method - 0x30);
                            if ((newLhd.flags & LHD_WINDOWMASK) <= (4 * 32)) {
                                System.out.print("" + ((newLhd.flags & LHD_WINDOWMASK) >> 5) + 'a');
                            } else {
                                System.out.print(" ");
                            }
                            System.out.printf(" %d.%d", newLhd.unpVer / 10, newLhd.unpVer % 10);

                            if ((newLhd.flags & LHD_SPLIT_BEFORE) == 0) {
                                totalUnpSize += newLhd.unpSize;
                                fileCount++;
                            }
                            totalPackSize += newLhd.packSize;
                        }
                        arcPtr.seek(nextBlockPos);
                    }
                    if (outHeader != 0) {
                        System.err.println();
                        for (i = 0; i < 79; i++) {
                            System.out.print("-");
                        }
                        System.out.printf("\n%5d %16d %8d %3d%%\n",
                            fileCount, totalUnpSize,
                            totalPackSize,
                            toPercent(totalPackSize, totalUnpSize)
                        );
                        sumFileCount += fileCount;
                        sumUnpSize += totalUnpSize;
                        sumPackSize += totalPackSize;
                    } else {
                        Debug.print(rb.getString("message.ListNoFiles"));
                    }

                    archivesCount++;

                    if ((opt.packVolume != 0) &&
                        ((newLhd.flags & LHD_SPLIT_AFTER) != 0) &&
                        (mergeArchive(0) != 0)) {
                        arcPtr.seek(0);
                    } else {
                        break;
                    }
                } else {
                    if (totalArcCount < 2) {
                        Debug.printf(rb.getString("message.NotRAR"), arcName);
                    }
                    break;
                }
            }
            arcPtr.close();
        }
        if (archivesCount > 1) {
            Debug.printf("\n%5lu %16lu %8lu %3d%%\n", sumFileCount, sumUnpSize,
                    sumPackSize, toPercent(sumPackSize, sumUnpSize));
        }
    }

// os

    /**
     * Display ANSI comments
     */
    private void showAnsiComment(String comment) {
        System.out.println(comment);
    }

    /**
     * Input string for password
     */
    private String getPswStr() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    /** */
    private boolean splitPath(String fullName, String path, String name, int removeDrive) {
        int chPtr;
        if ((removeDrive != 0) && ((chPtr = fullName.indexOf(':')) != -1)) {
            chPtr++;
        } else {
            chPtr = 0;
        }
        name = pointToName(fullName.substring(chPtr + 1));
        path = fullName.substring(chPtr);
        path.substring(fullName.length() - chPtr - name.length());
        if ((fullName.indexOf(chPtr, '?') != -1) || (fullName.indexOf(chPtr, '*') != -1)) {
            return true;
        } else {
            return false;
        }
    }

    /** */
    private int getPathDisk(String path) {
        if (Character.isLetter(path.charAt(0)) && (path.charAt(1) == ':')) {
            return Character.toUpperCase(path.charAt(0)) - 'A';
        } else {
            return -1;
        }
    }

    /** */
    private boolean isPathDiv(int ch) {
        if ((ch == '\\') || (ch == '/')) {
            return true;
        }
        return false;
    }

    /** */
    private boolean isDriveDiv(int ch) {
        return (ch == ':');
    }

    /** */
    private void convertUnknownHeader() {
        if (newLhd.hostOS > UNIX) {
            newLhd.hostOS = MS_DOS;
            if ((newLhd.flags & LHD_WINDOWMASK) == LHD_DIRECTORY) {
                newLhd.fileAttr = 0x10;
            } else {
                newLhd.fileAttr = 0x20;
            }
        }
        for (int i = 0; i < newLhd.name.length(); i++) {
            if (isPathDiv(newLhd.name.charAt(i))) {
                newLhd.name.replace(File.pathSeparator, "");
            }
        }
    }

// others

    /** */
    private String[] nextArgName;

    /**
     * @throws ParseException
     * @throws IOException
     */
    private void executeCommand(String[] args) throws ParseException, IOException {
        Options options = new Options();
        options.addOption("l", false, "");
        options.addOption("x", false, "");
        options.addOption("f", false, "");
        options.addOption("m", false, "");
        options.addOption("?", false, "");

        CommandLineParser parser = new BasicParser();

        CommandLine cl = parser.parse(options, args);
        HelpFormatter formatter = new HelpFormatter();

        if (cl.hasOption("?")) {
            formatter.printHelp("unrar", options, true);
            errExit(EEMPTY, USER_ERROR);
        }

        mainCommand = Character.toUpperCase(args[0].charAt(0));
Debug.println("mainCommand: " + mainCommand);
        nextArgName = cl.getArgs();

        switch (mainCommand) {
        case 'P':
        case 'X':
        case 'E':
        case 'T':
            argsUsed++;
            extrFile();
            break;
        case 'V':
        case 'L':
            argsUsed++;
            listArchive();
            break;
        default:
            formatter.printHelp("unrar", options, true);
            errExit(EEMPTY, USER_ERROR);
            break;
        }
    }

    /** */
    private void shutDown(int mode) throws IOException {
        password = null;
        if ((mode & SD_FILES) != 0) {
            if (arcPtr != null) {
                arcPtr.close();
            }
            if (tmpArcPtr != null) {
                tmpArcPtr.close();
            }
            if (fileInPtr != null) {
                fileInPtr.close();
            }
            if (fileOutPtr != null) {
                fileOutPtr.close();
            }
            argsUsed++;
            if ((curExtrFile != null) &&
                ((mainCommand == 'X') ||
                (mainCommand == 'E'))) {
                new File(curExtrFile).delete();
            }
        }
    }

// rdwrfn

    /** */
    private int unpRead(byte[] addr, int off, int count) throws IOException {
        int retCode = 0;
        int readSize;
        int totalRead = 0;
        byte[] readAddr = addr;
        while (count > 0) {
            readSize = (int) ((count > unpPackedSize) ? unpPackedSize : count);
            if (arcPtr == null) {
                return 0;
            }
            retCode = rdUnpPtr.read(readAddr, off, readSize);
            if ((newLhd.flags & LHD_SPLIT_AFTER) != 0) {
                packedCRC = RarCRC.checkCrc(packedCRC, readAddr, off, readSize);
            }
            curUnpRead += retCode;
            totalRead += retCode;
            count -= retCode;
            unpPackedSize -= retCode;
            if ((unpPackedSize == 0) && (unpVolume != 0)) {
                mergeArchive(1);
            } else {
                break;
            }
        }
        if (retCode != -1) {
            retCode = totalRead;
            if (encryption != 0) {
                if (encryption < 20) {
                    crypt(addr, retCode + off,
                          (encryption == 15) ? NEW_CRYPT : OLD_DECODE);
                } else {
                    for (int i = 0; i < retCode; i += 16) {
                        decryptBlock(addr, off + i);
                    }
                }
            }
        }
        return retCode;
    }

    /** */
    private void unpWrite(byte[] addr, int off, int count) throws IOException {
        unpWrAddr = addr;
        unpWrSize = count;
        if (testMode == 0) {
            wrUnpPtr.write(addr, off, count);
        }
        curUnpWrite += count;
        if (skipUnpCRC == 0) {
            if (arcFormat == OLD) {
                unpFileCRC = RarCRC.checkCrc(unpFileCRC, addr, off, count);
            } else {
                unpFileCRC = RarCRC.checkCrc(unpFileCRC, addr, off, count);
            }
        }
    }

// smallfn

    /** */
    private void tunpack(byte[] mem, int solid, int mode) throws IOException {
        if (mode == OLD_UNPACK) {
            oldUnpack(mem, solid);
        } else {
            unpack(mem, solid);
        }
    }

    /** */
    private OutputStream fileCreate(String name, int overwriteMode, int[] userReject) throws IOException {
        if (userReject != null) {
            userReject[0] = 0;
        }
        while (new File(name).exists()) {
            if (overwriteMode == NO_OVERWR) {
                if (userReject != null) {
                    userReject[0] = 1;
                }
                return null;
            }
            if ((opt.allYes != 0) || (overwriteAll != 0) ||
                (overwriteMode == ALL_OVERWR)) {
                break;
            }
            if (overwriteMode == ASK_OVERWR) {
                Debug.printf(rb.getString("message.FileExists"), name);
                ask(rb.getString("message.YesNoAllRenQ"));
                if (choice == 1) {
                    break;
                }
                if (choice == 2) {
                    if (userReject != null) {
                        userReject[0] = 1;
                    }
                    return null;
                }
                if (choice == 3) {
                    overwriteAll = 1;
                    break;
                }
                if (choice == 4) {
                    Debug.print(rb.getString("message.AskNewName"));
                    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                    name = r.readLine();
                    continue;
                }
                if (choice == 5) {
                    errExit(EEMPTY, USER_BREAK);
                }
            }
        }
        try {
            return Files.newOutputStream(Paths.get(name));
        } catch (IOException e) {
            new File(name).mkdirs();
            return Files.newOutputStream(Paths.get(name));
        }
    }

    /** */
    private int getKey() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String line = r.readLine();
        return line.charAt(0);
    }

    /** */
    private String pointToName(String path) {
        for (int i = path.length() - 1; i >= 0; i--) {
            if (isPathDiv(path.charAt(i))) {
                return path.substring(i + 1);
            }
        }
        int chPtr;
        if ((chPtr = path.indexOf(':')) != -1) {
            return path.substring(chPtr + 1);
        } else {
            return path;
        }
    }

    /** */
    private void setExt(String name, String newExt) {
        if (name.indexOf('.') == -1) {
            name += ".";
            name += newExt;
        } else {
            name += newExt;
        }
    }

    /** */
    private int getPassword(int askCount) throws IOException {
        int retCode;
        String cmpStr;
        String promptStr = rb.getString("message.AskPsw");
        if (askCount == 1) {
            promptStr += rb.getString("message.For");
            promptStr += pointToName(newLhd.name);
        }
        System.out.printf("\n%s: ", promptStr);
        password = getPswStr();
        if (password.charAt(0) != 0) {
            if (askCount == 1) {
                return 1;
            }
            System.out.print(rb.getString("message.ReAskPsw"));
            cmpStr = getPswStr();
            if (cmpStr.charAt(0) == 0) {
                retCode = -1;
            } else {
                if (password.equals(cmpStr)) {
                    System.out.print(rb.getString("message.NotMatchPsw"));
                    retCode = 0;
                } else {
                    retCode = 1;
                }
            }
        } else {
            retCode = -1;
        }
        return retCode;
    }

    /** */
    private void ask(String askStr) throws IOException {
        String[] item = new String[5];
        int chPtr;
        int numItems = 0;
        int ch;
        int i;

        String askString = askStr;
        while ((chPtr = askString.indexOf('_')) != -1) {
            item[numItems] = askString.substring(chPtr + 1, askString.length());
            item[numItems] += ' ';
            if ((chPtr = item[numItems].indexOf('_')) != -1) {
                chPtr = 0;
            }
            if (item[numItems].equals(rb.getString("message.CmpYesStr")) &&
                opt.allYes != 0) {
                choice = numItems + 1;
                return;
            }
            numItems++;
        }
        System.out.printf("  %s", item[0]);
        for (i = 1; i < numItems; i++) {
            System.out.printf("/%s", item[i]);
        }
        System.out.print(" ");
        ch = Character.toUpperCase(getKey());
        for (choice = 0, i = 1; i <= numItems; i++) {
            if (ch == item[i - 1].charAt(0)) {
                choice = i;
            }
        }
    }

    /** */
    private int toPercent(long n1, long n2) {
        if (n1 > 10000) {
            n1 /= 100;
            n2 /= 100;
        }
        if (n2 == 0) {
            return 0;
        }
        if (n2 < n1) {
            return 100;
        }
        return (int) ((n1 * 100) / n2);
    }

//  somefn

    /** archive files */
    private void getArcNames() {
        arcNamesSize = arcCount = 0;
        splitPath(arcName, findPath, findName, 0);
        findArchives();
        totalArcCount = arcCount;
        arcNamesSize = 0;
    }

    /** archive files */
    private void findArchives() {
        File dir = new File(findPath);

        if (dir.exists()) {
            File[] ent = new File(findPath).listFiles();
            for (int i = 0; i < ent.length; i++) {
                String archiveName = findPath;
                archiveName += ent[i].getName();
                File fs = new File(archiveName);
                if (fs.exists()) {
                    continue;
                }

                if (fs.isDirectory()) {
                    if (opt.recurse != 0 &&
                        !".".equals(ent[i].getName()) &&
                        !"..".equals(ent[i].getName())) {
                        findPath += ent[i].getName();
                        findPath += File.separator;
                        findArchives();
                    }
                } else {
                    if (findName.equals(ent[i].getParent())) {
                        int nameLen = archiveName.length() + 1;
                        arcName += archiveName;
                        arcNamesSize += nameLen;
                        arcCount++;
                    }
                }
            }
        }

        int chPtr;
        if ((chPtr = findPath.indexOf(File.pathSeparatorChar)) != -1) {
            chPtr = 0;
            if ((chPtr = findPath.indexOf(File.pathSeparatorChar)) != -1) {
                findPath = findPath.substring(0, chPtr);
            } else {
                findPath = null;
            }
        } else {
            findPath = null;
        }
    }

    /** */
    private int readArcName() {
        if (argsUsed < nextArgName.length) {
            arcName = nextArgName[argsUsed++];
Debug.println(arcName);
//        if (arcCount == 0) {
//            return 0;
//        }
        arcNamesSize = arcName.length();
            arcCount--;
            return 1;
        } else {
            return 0;
        }
    }

    /** */
    private int isArchive() throws IOException {
        long curPos;
        int readSize;
        int chPtr;
        sFXLen = arcType = solidType = lockedType = aVType = 0;
        arcFormat = mainComment = brokenMhd = 0;

        if (arcPtr.read(markHead.mark, 0, SIZEOF_MARKHEAD) != SIZEOF_MARKHEAD) {
            return 0;
        }
Debug.println("markHead:\n" + StringUtil.getDump(markHead.mark));

        if ((markHead.mark[0] == 0x52) && (markHead.mark[1] == 0x45) &&
            (markHead.mark[2] == 0x7e) && (markHead.mark[3] == 0x5e)) {
            arcFormat = OLD;
            arcPtr.seek(0);
            readHeader(MAIN_HEAD);
            arcType = ((oldMhd.flags & MHD_MULT_VOL) != 0) ? VOL : ARC;
        } else {
            if ((markHead.mark[0] == 0x52) && (markHead.mark[1] == 0x61) &&
                (markHead.mark[2] == 0x72) && (markHead.mark[3] == 0x21) &&
                (markHead.mark[4] == 0x1a) && (markHead.mark[5] == 0x07) &&
                (markHead.mark[6] == 0x00)) {
                arcFormat = NEW;
                if (readHeader(MAIN_HEAD) != SIZEOF_NEWMHD) {
                    return 0;
                }
Debug.println("newMhd:\n" + StringUtil.paramString(newMhd));

                if ((newMhd.flags & MHD_MULT_VOL) != 0) {
                    arcType = VOL;
                } else {
                    arcType = ARC;
                }
            } else {
                tempMemory = new byte[0x20000];
                curPos = arcPtr.getFilePointer();
                readSize = arcPtr.read(tempMemory, 0, 0x1fff0);
                chPtr = 0; // tempmemory
                while ((arcType == 0) && (chPtr != 0) &&
                       (chPtr < (0 + readSize))) {
                    for (; tempMemory[chPtr] == 0x52 && chPtr < readSize; chPtr++) {
                        ;
                    }
                    if (chPtr < readSize) {
                        chPtr++;
                        if ((tempMemory[chPtr + 0] == 0x45) && (tempMemory[chPtr + 1] == 0x7e) &&
                            (tempMemory[chPtr + 2] == 0x5e) &&
                            (tempMemory[(int) (28 - curPos)] == 0x52) &&
                            (tempMemory[(int) (29 - curPos)] == 0x53) &&
                            (tempMemory[(int) (30 - curPos)] == 0x46) &&
                            (tempMemory[(int) (31 - curPos)] == 0x58)) {
                            arcFormat = OLD;
                            sFXLen = (int) ((curPos + chPtr) - 0 - 1);
                            arcPtr.seek(sFXLen);
                            readHeader(MAIN_HEAD);
                            if ((oldMhd.flags & MHD_MULT_VOL) != 0) {
                                arcType = VOL;
                            } else {
                                arcType = SFX;
                            }
                        }
                        if ((tempMemory[chPtr + 0] == 0x61) && (tempMemory[chPtr + 1] == 0x72) &&
                            (tempMemory[chPtr + 2] == 0x21) && (tempMemory[chPtr + 3] == 0x1a) &&
                            (tempMemory[chPtr + 4] == 0x07) && (tempMemory[chPtr + 5] == 0x00) &&
                            (tempMemory[chPtr + 8] == MAIN_HEAD)) {
                            arcFormat = NEW;
                            sFXLen = (int) ((curPos + chPtr) - 0 - 1);
                            arcPtr.seek(sFXLen);
                            arcPtr.read(markHead.mark, 0, SIZEOF_MARKHEAD);
                            readHeader(MAIN_HEAD);
                            if ((newMhd.flags & MHD_MULT_VOL) != 0) {
                                arcType = VOL;
                            } else {
                                arcType = SFX;
                            }
                        }
                    }
                }
                if (arcType == 0) {
Debug.println("arcType: 0");
                    return 0;
                }
            }
        }

        if (arcFormat == OLD) {
            mainHeadSize = SIZEOF_OLDMHD;
            newMhd.flags = oldMhd.flags & 0x3f;
            newMhd.headSize = oldMhd.headSize;
        } else {
            mainHeadSize = SIZEOF_NEWMHD;
            if ((~headerCRC & 0xffff) != (newMhd.headCRC & 0xffff)) {
                Debug.printf("\n\n%s %08x, %08x\n", rb.getString("message.LogMainHead"), ~((int) headerCRC), newMhd.headCRC);
                brokenMhd = 1;
            }
        }
        if ((newMhd.flags & MHD_SOLID) != 0) {
            solidType = 1;
        }
        if ((newMhd.flags & MHD_COMMENT) != 0) {
            mainComment = 1;
        }
        if ((newMhd.flags & MHD_LOCK) != 0) {
            lockedType = 1;
        }
        if ((newMhd.flags & MHD_AV) != 0) {
            aVType = 1;
        }
Debug.println("arcType: " + arcType);
        return arcType;
    }

    /** unpack */
    private static class Decode {
        int maxNum;
        int[] decodeLen = new int[16];
        int[] decodePos = new int[16];
        int[] decodeNum;

        Decode(int n) {
            decodeNum = new int[n];
        }
    }

    /** */
    private AudioVariables[] audV = new AudioVariables[4];

    /** */
    private void getBits() {
        bitField = (int) (((((long) inBuf[inAddr] << 16) |
                   ((long) inBuf[inAddr + 1] << 8) | (inBuf[inAddr + 2])) >> (8 -
                   inBit)) & 0xffff);
    }

    /** */
    private void addBits(int bits) {
        inAddr += ((inBit + (bits)) >> 3);
        inBit = (inBit + (bits)) & 7;
    }

    /** */
    private int suspend;
    private byte[] unpBuf;
    private int unpPtr;
    private int wrPtr;
    private int bitField;
    private int number;
    private byte[] inBuf = new byte[8192];
    private byte[] unpOldTable = new byte[MC * 4];
    private int inAddr;
    private int inBit;
    private int readTop;
    private int lastDist;
    private int lastLength;
    private int length;
    private int distance;
    private int[] oldDist = new int[4];
    private int oldDistPtr;
    private Decode ld = new Decode(NC);
    private Decode dd = new Decode(DC);
    private Decode rd = new Decode(RC);

    /** */
    private Decode[] md = {
        new Decode(MC), new Decode(MC), new Decode(MC), new Decode(MC)
    };

    /** */
    private Decode bd = new Decode(BC);

    /** */
    private int unpAudioBlock;

    /** */
    private int unpChannels;

    /** */
    private int curChannel;

    /** */
    private int channelDelta;

    /** */
    private static final char[] lDecode = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20,
        24, 28, 32, 40, 48, 56, 64, 80, 96, 112, 128,
        160, 192, 224
    };

    /** */
    private static final char[] lBits = {
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
        3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5
    };

    /** */
    private static final int[] dDecode = {
        0, 1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64,
        96, 128, 192, 256, 384, 512, 768, 1024, 1536,
        2048, 3072, 4096, 6144, 8192, 12288, 16384,
        24576, 32768, 49152, 65536, 98304, 131072,
        196608, 262144, 327680, 393216, 458752, 524288,
        589824, 655360, 720896, 786432, 851968, 917504,
        983040
    };

    /** */
    private static final char[] dBits = {
        0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6,
        7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13,
        13, 14, 14, 15, 15, 16, 16, 16, 16, 16, 16, 16,
        16, 16, 16, 16, 16, 16, 16
    };

    /** */
    private static final char[] sDDecode = { 0, 4, 8, 16, 32, 64, 128, 192 };

    /** */
    private static final char[] sDBits = { 2, 2, 3, 4, 5, 6, 6, 6 };

    /** */
    private void unpack(byte[] unpAddr, int solid) throws IOException {
        int bits;

        unpBuf = unpAddr;

        unpInitData(solid);
        unpReadBuf(1);
        if (solid == 0) {
            readTables();
        }
        destUnpSize--;

        while (destUnpSize >= 0) {
            unpPtr &= MAXWINMASK;

            if (inAddr > (inBuf.length - 30)) {
                unpReadBuf(0);
            }
            if ((((wrPtr - unpPtr) & MAXWINMASK) < 270) && (wrPtr != unpPtr)) {
                unpWriteBuf();
            }
            if (unpAudioBlock != 0) {
                decodeNumber(md[curChannel]);
                if (number == 256) {
                    readTables();
                    continue;
                }
                unpBuf[unpPtr++] = decodeAudio(number);
                if (++curChannel == unpChannels) {
                    curChannel = 0;
                }
                destUnpSize--;
                continue;
            }

            decodeNumber(ld);
            if (number < 256) {
                unpBuf[unpPtr++] = (byte) number;
                destUnpSize--;
                continue;
            }
            if (number > 269) {
                length = lDecode[number -= 270] + 3;
                if ((bits = lBits[number]) > 0) {
                    getBits();
                    length += (bitField >> (16 - bits));
                    addBits(bits);
                }

                decodeNumber(dd);
                distance = dDecode[number] + 1;
                if ((bits = dBits[number]) > 0) {
                    getBits();
                    distance += (bitField >> (16 - bits));
                    addBits(bits);
                }

                if (distance >= 0x40000L) {
                    length++;
                }
                if (distance >= 0x2000) {
                    length++;
                }
                copyString();
                continue;
            }
            if (number == 269) {
                readTables();
                continue;
            }
            if (number == 256) {
                length = lastLength;
                distance = lastDist;
                copyString();
                continue;
            }
            if (number < 261) {
                distance = oldDist[(oldDistPtr - (number - 256)) & 3];
                decodeNumber(rd);
                length = lDecode[number] + 2;
                if ((bits = lBits[number]) > 0) {
                    getBits();
                    length += (bitField >> (16 - bits));
                    addBits(bits);
                }
                if (distance >= 0x40000) {
                    length++;
                }
                if (distance >= 0x2000) {
                    length++;
                }
                if (distance >= 0x101) {
                    length++;
                }
                copyString();
                continue;
            }
            if (number < 270) {
                distance = sDDecode[number -= 261] + 1;
                if ((bits = sDBits[number]) > 0) {
                    getBits();
                    distance += (bitField >> (16 - bits));
                    addBits(bits);
                }
                length = 2;
                copyString();
                continue;
            }
        }
        readLastTables();
        unpWriteBuf();
    }

    /** */
    private void unpReadBuf(int firstBuf) throws IOException {
        int retCode;
        if (firstBuf != 0) {
            readTop = unpRead(inBuf, 0, inBuf.length);
            inAddr = 0;
        } else {
            System.arraycopy(inBuf, 0, inBuf, inBuf.length - 32, 32);
            inAddr &= 0x1f;
            retCode = unpRead(inBuf, 32, inBuf.length - 32);
            if (retCode > 0) {
                readTop = retCode + 32;
            } else {
                if (readTop >= inBuf.length - 32) { // TODO
                    readTop -= inBuf.length - 32; // TODO
                }
            }
        }
    }

    /** */
    private void unpWriteBuf() throws IOException {
        if (unpPtr < wrPtr) {
            unpWrite(unpBuf, wrPtr, -wrPtr & MAXWINMASK);
            unpWrite(unpBuf, 0, unpPtr);
        } else {
            unpWrite(unpBuf, wrPtr, unpPtr - wrPtr);
        }
        wrPtr = unpPtr;
    }

    /** */
    private void readTables() throws IOException {
        byte[] bitLength = new byte[BC];
        byte[] table = new byte[MC * 4];
        int tableSize;
        int n;
        int i;
        if (inAddr > (inBuf.length - 25)) {
            unpReadBuf(0);
        }
        getBits();
        unpAudioBlock = (bitField & 0x8000);

        if ((bitField & 0x4000) == 0) {
            unpOldTable = null;
        }
        addBits(2);

        if (unpAudioBlock != 0) {
            unpChannels = ((bitField >> 12) & 3) + 1;
            if (curChannel >= unpChannels) {
                curChannel = 0;
            }
            addBits(2);
            tableSize = MC * unpChannels;
        } else {
            tableSize = NC + DC + RC;
        }

        for (i = 0; i < BC; i++) {
            getBits();
            bitLength[i] = (byte) (bitField >> 12);
            addBits(4);
        }
        makeDecodeTables(bitLength, 0, bd, BC);
        i = 0;
        while (i < tableSize) {
            if (inAddr > inBuf.length - 5) {
                unpReadBuf(0);
            }
            decodeNumber(bd);
            if (number < 16) {
                table[i] = (byte) ((number + unpOldTable[i]) & 0xf);
                i++;
            } else {
                if (number == 16) {
                    getBits();
                    n = (bitField >> 14) + 3;
                    addBits(2);
                    while ((n-- > 0) && (i < tableSize)) {
                        table[i] = table[i - 1];
                        i++;
                    }
                } else {
                    if (number == 17) {
                        getBits();
                        n = (bitField >> 13) + 3;
                        addBits(3);
                    } else {
                        getBits();
                        n = (bitField >> 9) + 11;
                        addBits(7);
                    }
                    while ((n-- > 0) && (i < tableSize)) {
                        table[i++] = 0;
                    }
                }
            }
        }
        if (inAddr > readTop) {
            return;
        }
        if (unpAudioBlock != 0) {
            for (i = 0; i < unpChannels; i++) {
                makeDecodeTables(table, i * MC, md[i], MC);
            }
        } else {
            makeDecodeTables(table, 0, ld, NC);
            makeDecodeTables(table, NC, dd, DC);
            makeDecodeTables(table, NC + DC, rd, RC);
        }
        unpOldTable = table;
    }

    /** */
    private void readLastTables() throws IOException {
        if (readTop >= (inAddr + 5)) {
            if (unpAudioBlock != 0) {
                decodeNumber(md[curChannel]);
                if (number == 256) {
                    readTables();
                }
            } else {
                decodeNumber(ld);
                if (number == 269) {
                    readTables();
                }
            }
        }
    }

    /** */
    private void makeDecodeTables(byte[] lenTab, int off, Decode dec, int size) {
        int[] lenCount = new int[16];
        int[] tmpPos = new int[16];
        int i;
        long m;
        long n;

        for (i = 0; i < size; i++) {
            lenCount[lenTab[off + i] & 0x0f]++;
        }
        lenCount[0] = 0;
        for (tmpPos[0] = dec.decodePos[0] = dec.decodeLen[0] = 0, n = 0, i = 1;
             i < 16; i++) {
            n = 2 * (n + lenCount[i]);
            m = n << (15 - i);
            if (m > 0xffff) {
                m = 0xffff;
            }
            dec.decodeLen[i] = (int) m;
            tmpPos[i] = dec.decodePos[i] = dec.decodePos[i - 1] +
                                           lenCount[i - 1];
        }

        for (i = 0; i < size; i++) {
            if (lenTab[off + i] != 0) {
                dec.decodeNum[tmpPos[lenTab[off + i] & 0x0f]++] = i;
            }
        }
        dec.maxNum = size;
    }

    /** */
    private void decodeNumber(Decode dec) {
        int i;
        int n;
        getBits();
        n = bitField & 0xfffe;
        if (n < dec.decodeLen[8]) {
            if (n < dec.decodeLen[4]) {
                if (n < dec.decodeLen[2]) {
                    if (n < dec.decodeLen[1]) {
                        i = 1;
                    } else {
                        i = 2;
                    }
                } else {
                    if (n < dec.decodeLen[3]) {
                        i = 3;
                    } else {
                        i = 4;
                    }
                }
            } else {
                if (n < dec.decodeLen[6]) {
                    if (n < dec.decodeLen[5]) {
                        i = 5;
                    } else {
                        i = 6;
                    }
                } else {
                    if (n < dec.decodeLen[7]) {
                        i = 7;
                    } else {
                        i = 8;
                    }
                }
            }
        } else {
            if (n < dec.decodeLen[12]) {
                if (n < dec.decodeLen[10]) {
                    if (n < dec.decodeLen[9]) {
                        i = 9;
                    } else {
                        i = 10;
                    }
                } else {
                    if (n < dec.decodeLen[11]) {
                        i = 11;
                    } else {
                        i = 12;
                    }
                }
            } else {
                if (n < dec.decodeLen[14]) {
                    if (n < dec.decodeLen[13]) {
                        i = 13;
                    } else {
                        i = 14;
                    }
                } else {
                    i = 15;
                }
            }
        }

        addBits(i);
        if ((n = dec.decodePos[i] + ((n - dec.decodeLen[i - 1]) >> (16 - i))) >= dec.maxNum) {
            n = 0;
        }
        number = dec.decodeNum[n];
    }

    /** */
    private void copyString() {
        lastDist = oldDist[oldDistPtr++ & 3] = distance;
        destUnpSize -= (lastLength = length);
        while (length-- > 0) {
            unpBuf[unpPtr] = unpBuf[(unpPtr - distance) & MAXWINMASK];
            unpPtr = (unpPtr + 1) & MAXWINMASK;
        }
    }

    /** */
    private void unpInitData(int solid) {
        inAddr = inBit = 0;
        if (solid != 0) {
            channelDelta = curChannel = 0;
            audV = null;
            oldDist = null;
            oldDistPtr = 0;
            lastDist = lastLength = 0;
            unpBuf = null;
            unpOldTable = null;
            unpPtr = wrPtr = 0;
        }
    }

    /** */
    private byte decodeAudio(int delta) {
        AudioVariables v;
        int ch;
        int numMinDif;
        int minDif;
        int pCh;
        int i;

        v = audV[curChannel];
        v.byteCount++;
        v.d4 = v.d3;
        v.d3 = v.d2;
        v.d2 = v.lastDelta - v.d1;
        v.d1 = v.lastDelta;
        pCh = (8 * v.lastChar) + (v.k1 * v.d1) + (v.k2 * v.d2) + (v.k3 * v.d3) +
              (v.k4 * v.d4) + (v.k5 * channelDelta);
        pCh = (pCh >> 3) & 0xff;

        ch = pCh - delta;

        i = delta << 3;

        v.dif[0] += Math.abs(i);
        v.dif[1] += Math.abs(i - v.d1);
        v.dif[2] += Math.abs(i + v.d1);
        v.dif[3] += Math.abs(i - v.d2);
        v.dif[4] += Math.abs(i + v.d2);
        v.dif[5] += Math.abs(i - v.d3);
        v.dif[6] += Math.abs(i + v.d3);
        v.dif[7] += Math.abs(i - v.d4);
        v.dif[8] += Math.abs(i + v.d4);
        v.dif[9] += Math.abs(i - channelDelta);
        v.dif[10] += Math.abs(i + channelDelta);

        channelDelta = v.lastDelta = (ch - v.lastChar);
        v.lastChar = ch;

        if ((v.byteCount & 0x1f) == 0) {
            minDif = v.dif[0];
            numMinDif = 0;
            v.dif[0] = 0;
            for (i = 1; i < v.dif.length; i++) {
                if (v.dif[i] < minDif) {
                    minDif = v.dif[i];
                    numMinDif = i;
                }
                v.dif[i] = 0;
            }
            switch (numMinDif) {
            case 1:
                if (v.k1 >= -16) {
                    v.k1--;
                }
                break;
            case 2:
                if (v.k1 < 16) {
                    v.k1++;
                }
                break;
            case 3:
                if (v.k2 >= -16) {
                    v.k2--;
                }
                break;
            case 4:
                if (v.k2 < 16) {
                    v.k2++;
                }
                break;
            case 5:
                if (v.k3 >= -16) {
                    v.k3--;
                }
                break;
            case 6:
                if (v.k3 < 16) {
                    v.k3++;
                }
                break;
            case 7:
                if (v.k4 >= -16) {
                    v.k4--;
                }
                break;
            case 8:
                if (v.k4 < 16) {
                    v.k4++;
                }
                break;
            case 9:
                if (v.k5 >= -16) {
                    v.k5--;
                }
                break;
            case 10:
                if (v.k5 < 16) {
                    v.k5++;
                }
                break;
            }
        }
        return (byte) ch;
    }

// unpold

    private int flagBuf;
    private int lCount;
    private int flagsCnt;
    private int[] chSet = new int[256];
    private int[] place = new int[256];
    private int[] nToPl = new int[256];
    private int[] chSetA = new int[256];
    private int[] placeA = new int[256];
    private int[] chSetB = new int[256];
    private int[] placeB = new int[256];
    private int[] nToPlB = new int[256];
    private int[] chSetC = new int[256];
    private int[] placeC = new int[256];
    private int[] nToPlC = new int[256];
    private int avrPlc;
    private int avrPlcB;
    private int avrLn1;
    private int avrLn2;
    private int avrLn3;
    private int numHuf;
    private int stMode;
    private int nhfb;
    private int nlzb;
    private int maxDist3;
    private int buf60;
    private static final int[] shortLen1 = { 1, 3, 4, 4, 5, 6, 7, 8, 8, 4, 4, 5, 6, 6, 4, 0 };
    private static final int[] shortXor1 = {
        0, 0xa0, 0xd0, 0xe0, 0xf0, 0xf8, 0xfc, 0xfe,
        0xff, 0xc0, 0x80, 0x90, 0x98, 0x9c, 0xb0
    };
    private static final int[] shortLen2 = { 2, 3, 3, 3, 4, 4, 5, 6, 6, 4, 4, 5, 6, 6, 4, 0 };
    private static final int[] shortXor2 = {
        0, 0x40, 0x60, 0xa0, 0xd0, 0xe0, 0xf0, 0xf8,
        0xfc, 0xc0, 0x80, 0x90, 0x98, 0x9c, 0xb0
    };
    private static final int STARTL1 = 2;
    private static final int[] decL1 = {
        0x8000, 0xa000, 0xc000, 0xd000, 0xe000, 0xea00,
        0xee00, 0xf000, 0xf200, 0xf200, 0xffff
    };
    private static final int[] posL1 = { 0, 0, 0, 2, 3, 5, 7, 11, 16, 20, 24, 32, 32 };
    private static final int STARTL2 = 3;
    private static final int[] decL2 = {
        0xa000, 0xc000, 0xd000, 0xe000, 0xea00, 0xee00,
        0xf000, 0xf200, 0xf240, 0xffff
    };
    private static final int[] posL2 = { 0, 0, 0, 0, 5, 7, 9, 13, 18, 22, 26, 34, 36 };
    private static final int STARTHF0 = 4;
    private static final int[] decHf0 = {
        0x8000, 0xc000, 0xe000, 0xf200, 0xf200, 0xf200,
        0xf200, 0xf200, 0xffff
    };
    private static final int[] posHf0 = { 0, 0, 0, 0, 0, 8, 16, 24, 33, 33, 33, 33, 33 };
    private static final int STARTHF1 = 5;
    private static final int[] decHf1 = {
        0x2000, 0xc000, 0xe000, 0xf000, 0xf200, 0xf200,
        0xf7e0, 0xffff
    };
    private static final int[] posHf1 = { 0, 0, 0, 0, 0, 0, 4, 44, 60, 76, 80, 80, 127 };
    private static final int STARTHF2 = 5;
    private static final int[] decHf2 = {
        0x1000, 0x2400, 0x8000, 0xc000, 0xfa00, 0xffff,
        0xffff, 0xffff
    };
    private static final int[] posHf2 = { 0, 0, 0, 0, 0, 0, 2, 7, 53, 117, 233, 0, 0 };
    private static final int STARTHF3 = 6;
    private static final int[] decHf3 = { 0x800, 0x2400, 0xee00, 0xfe80, 0xffff, 0xffff, 0xffff };
    private static final int[] posHf3 = { 0, 0, 0, 0, 0, 0, 0, 2, 16, 218, 251, 0, 0 };
    private static final int STARTHF4 = 8;
    private static final int[] decHf4 = { 0xff00, 0xffff, 0xffff, 0xffff, 0xffff, 0xffff };
    private static final int[] posHf4 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 0, 0, 0 };
    private static final int MAXWINMASK = 0;

    /** */
    private void oldUnpack(byte[] unpAddr, int solid) throws IOException {
        int setStMode;
        unpBuf = unpAddr;
        if (solid == 0x1000) {
            solid = 0;
            setStMode = 1;
        } else {
            setStMode = 0;
        }
        if (suspend != 0) {
            unpPtr = wrPtr;
        } else {
            unpInitData(solid);
            oldunpInitData(solid);
            unpReadBuf(1);
            if (solid == 0) {
                initHuff();
                unpPtr = 0;
            } else {
                unpPtr = wrPtr;
            }
            destUnpSize--;
        }
        if (setStMode != 0) {
            stMode = 1;
        } else {
            if (destUnpSize >= 0) {
                getflagsBuf();
                flagsCnt = 8;
            }
        }

        while (destUnpSize >= 0) {
            unpPtr &= MAXWINMASK;

            if (inAddr > (inBuf.length - 30)) {
                unpReadBuf(0);
            }
            if ((((wrPtr - unpPtr) & MAXWINMASK) < 270) && (wrPtr != unpPtr)) {
                unpWriteBuf();
                if (suspend != 0) {
                    return;
                }
            }
            if (stMode != 0) {
                huffDecode();
                continue;
            }

            if (--flagsCnt < 0) {
                getflagsBuf();
                flagsCnt = 7;
            }

            if ((flagBuf & 0x80) != 0) {
                flagBuf <<= 1;
                if (nlzb > nhfb) {
                    longLZ();
                } else {
                    huffDecode();
                }
            } else {
                flagBuf <<= 1;
                if (--flagsCnt < 0) {
                    getflagsBuf();
                    flagsCnt = 7;
                }
                if ((flagBuf & 0x80) != 0) {
                    flagBuf <<= 1;
                    if (nlzb > nhfb) {
                        huffDecode();
                    } else {
                        longLZ();
                    }
                } else {
                    flagBuf <<= 1;
                    shortLZ();
                }
            }
        }
        unpWriteBuf();
    }

    /** */
    private void shortLZ() {
        int length;
        int saveLength;
        int lastDistance;
        int distance;
        int distancePlace;
        numHuf = 0;
        getBits();
        if (lCount == 2) {
            addBits(1);
            if (bitField >= 0x8000) {
                oldCopyString(lastDist, lastLength);
                return;
            }
            bitField <<= 1;
            lCount = 0;
        }

        bitField >>= 8;

        shortLen1[1] = shortLen2[3] = buf60 + 3;

        if (avrLn1 < 37) {
            for (length = 0;; length++) {
                if (((bitField ^ shortXor1[length]) &
                    (~(0xff >> shortLen1[length]))) == 0) {
                    break;
                }
            }
            addBits(shortLen1[length]);
        } else {
            for (length = 0;; length++) {
                if (((bitField ^ shortXor2[length]) &
                    (~(0xff >> shortLen2[length]))) == 0) {
                    break;
                }
            }
            addBits(shortLen2[length]);
        }

        if (length >= 9) {
            if (length == 9) {
                lCount++;
                oldCopyString(lastDist, lastLength);
                return;
            }
            if (length == 14) {
                lCount = 0;
                getBits();
                length = decodeNum(bitField, STARTL2, decL2, posL2) + 5;
                getBits();
                distance = (bitField >> 1) | 0x8000;
                addBits(15);
                lastLength = length;
                lastDist = distance;
                oldCopyString(distance, length);
                return;
            }

            lCount = 0;
            saveLength = length;
            distance = oldDist[(oldDistPtr - (length - 9)) & 3];
            getBits();
            length = decodeNum(bitField, STARTL1, decL1, posL1) + 2;
            if ((length == 0x101) && (saveLength == 10)) {
                buf60 ^= 1;
                return;
            }
            if (distance > 256) {
                length++;
            }
            if (distance >= maxDist3) {
                length++;
            }

            oldDist[oldDistPtr++] = distance;
            oldDistPtr = oldDistPtr & 3;
            lastLength = length;
            lastDist = distance;
            oldCopyString(distance, length);
            return;
        }

        lCount = 0;
        avrLn1 += length;
        avrLn1 -= (avrLn1 >> 4);

        getBits();
        distancePlace = decodeNum(bitField, STARTHF2, decHf2, posHf2) & 0xff;
        distance = chSetA[distancePlace];
        if (--distancePlace != -1) {
            placeA[distance]--;
            lastDistance = chSetA[distancePlace];
            placeA[lastDistance]++;
            chSetA[distancePlace + 1] = lastDistance;
            chSetA[distancePlace] = distance;
        }
        length += 2;
        oldDist[oldDistPtr++] = ++distance;
        oldDistPtr = oldDistPtr & 3;
        lastLength = length;
        lastDist = distance;
        oldCopyString(distance, length);
        return;
    }

    /** */
    private void longLZ() {
        int length;
        int distance;
        int distancePlace;
        int newDistancePlace;
        int oldavr2;
        int oldavr3;

        numHuf = 0;
        nlzb += 16;
        if (nlzb > 0xff) {
            nlzb = 0x90;
            nhfb >>= 1;
        }
        oldavr2 = avrLn2;

        getBits();
        if (avrLn2 >= 122) {
            length = decodeNum(bitField, STARTL2, decL2, posL2);
        } else {
            if (avrLn2 >= 64) {
                length = decodeNum(bitField, STARTL1, decL1, posL1);
            } else {
                if (bitField < 0x100) {
                    length = bitField;
                    addBits(16);
                } else {
                    for (length = 0; ((bitField << length) & 0x8000) == 0;
                         length++) {
                        ;
                    }
                    addBits(length + 1);
                }
            }
        }
        avrLn2 += length;
        avrLn2 -= (avrLn2 >> 5);

        getBits();
        if (avrPlcB > 0x28ff) {
            distancePlace = decodeNum(bitField, STARTHF2, decHf2, posHf2);
        } else {
            if (avrPlcB > 0x6ff) {
                distancePlace = decodeNum(bitField, STARTHF1, decHf1, posHf1);
            } else {
                distancePlace = decodeNum(bitField, STARTHF0, decHf0, posHf0);
            }
        }
        avrPlcB += distancePlace;
        avrPlcB -= (avrPlcB >> 8);
        while (true) {
            distance = chSetB[distancePlace];
            newDistancePlace = nToPlB[distance++ & 0xff]++;
            if ((distance & 0xff) == 0) {
                corrHuff(chSetB, nToPlB);
            } else {
                break;
            }
        }

        chSetB[distancePlace] = chSetB[newDistancePlace];
        chSetB[newDistancePlace] = distance;

        getBits();
        distance = (((distance & 0xff00) | (bitField >> 8))) >> 1;
        addBits(7);

        oldavr3 = avrLn3;
        if ((length != 1) && (length != 4)) {
            if ((length == 0) && (distance <= maxDist3)) {
                avrLn3++;
                avrLn3 -= (avrLn3 >> 8);
            } else {
                if (avrLn3 > 0) {
                    avrLn3--;
                }
            }
        }
        length += 3;
        if (distance >= maxDist3) {
            length++;
        }
        if (distance <= 256) {
            length += 8;
        }
        if ((oldavr3 > 0xb0) || ((avrPlc >= 0x2a00) && (oldavr2 < 0x40))) {
            maxDist3 = 0x7f00;
        } else {
            maxDist3 = 0x2001;
        }
        oldDist[oldDistPtr++] = distance;
        oldDistPtr = oldDistPtr & 3;
        lastLength = length;
        lastDist = distance;
        oldCopyString(distance, length);
    }

    /** */
    private void huffDecode() {
        int curByte;
        int newBytePlace;
        int length;
        int distance;
        int bytePlace;

        getBits();

        if (avrPlc > 0x75ff) {
            bytePlace = decodeNum(bitField, STARTHF4, decHf4, posHf4);
        } else {
            if (avrPlc > 0x5dff) {
                bytePlace = decodeNum(bitField, STARTHF3, decHf3, posHf3);
            } else {
                if (avrPlc > 0x35ff) {
                    bytePlace = decodeNum(bitField, STARTHF2, decHf2, posHf2);
                } else {
                    if (avrPlc > 0x0dff) {
                        bytePlace = decodeNum(bitField, STARTHF1, decHf1, posHf1);
                    } else {
                        bytePlace = decodeNum(bitField, STARTHF0, decHf0, posHf0);
                    }
                }
            }
        }
        bytePlace &= 0xff;
        if (stMode != 0) {
            if ((bytePlace == 0) && (bitField > 0xfff)) {
                bytePlace = 0x100;
            }
            if (--bytePlace == -1) {
                getBits();
                addBits(1);
                if ((bitField & 0x8000) != 0) {
                    numHuf = stMode = 0;
                    return;
                } else {
                    length = ((bitField & 0x4000) != 0) ? 4 : 3;
                    addBits(1);
                    getBits();
                    distance = decodeNum(bitField, STARTHF2, decHf2, posHf2);
                    getBits();
                    distance = (distance << 5) | (bitField >> 11);
                    addBits(5);
                    oldCopyString(distance, length);
                    return;
                }
            }
        } else {
            if ((numHuf++ >= 16) && (flagsCnt == 0)) {
                stMode = 1;
            }
        }
        avrPlc += bytePlace;
        avrPlc -= (avrPlc >> 8);
        nhfb += 16;
        if (nhfb > 0xff) {
            nhfb = 0x90;
            nlzb >>= 1;
        }

        unpBuf[unpPtr++] = (byte) (chSet[bytePlace] >> 8);
        destUnpSize--;

        while (true) {
            curByte = chSet[bytePlace];
            newBytePlace = nToPl[curByte++ & 0xff]++;
            if ((curByte & 0xff) > 0xa1) {
                corrHuff(chSet, nToPl);
            } else {
                break;
            }
        }

        chSet[bytePlace] = chSet[newBytePlace];
        chSet[newBytePlace] = curByte;
    }

    /** */
    private void getflagsBuf() {
        int flags;
        int flagsPlace;
        int newFlagsPlace;

        getBits();
        flagsPlace = decodeNum(bitField, STARTHF2, decHf2, posHf2);

        while (true) {
            flags = chSetC[flagsPlace];
            flagBuf = flags >> 8;
            newFlagsPlace = nToPlC[flags++ & 0xff]++;
            if ((flags & 0xff) == 0) {
                corrHuff(chSetC, nToPlC);
            } else {
                break;
            }
        }

        chSetC[flagsPlace] = chSetC[newFlagsPlace];
        chSetC[newFlagsPlace] = flags;
    }

    /** */
    private void oldunpInitData(int solid) {
        if (solid == 0) {
            avrPlcB = avrLn1 = avrLn2 = avrLn3 = numHuf = buf60 = 0;
            avrPlc = 0x3500;
            maxDist3 = 0x2001;
            nhfb = nlzb = 0x80;
        }
        flagsCnt = 0;
        flagBuf = 0;
        stMode = 0;
        lCount = 0;
    }

    /** */
    private void initHuff() {
        for (int i = 0; i < 256; i++) {
            place[i] = placeA[i] = placeB[i] = i;
            placeC[i] = (~i + 1) & 0xff;
            chSet[i] = chSetB[i] = i << 8;
            chSetA[i] = i;
            chSetC[i] = ((~i + 1) & 0xff) << 8;
        }
        nToPl = null;
        nToPlB = null;
        nToPlC = null;
        corrHuff(chSetB, nToPlB);
    }

    /** */
    private void corrHuff(int[] charSet, int[] numToPlace) {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 32; j++) {
                charSet[j] = (charSet[j] & ~0xff) | i;
            }
        }
        numToPlace = nToPl;
        for (int i = 6; i >= 0; i--) {
            numToPlace[i] = (7 - i) * 32;
        }
    }

    /** */
    private void oldCopyString(int distance, int length) {
        destUnpSize -= length;
        while (length-- > 0) {
            unpBuf[unpPtr] = unpBuf[(unpPtr - distance) & MAXWINMASK];
            unpPtr = (unpPtr + 1) & MAXWINMASK;
        }
    }

    /** */
    private int decodeNum(int num, int startPos, int[] decTab, int[] posTab) {
        num &= 0xfff0;

        int i = 0;
        for (; decTab[i] <= num; i++) {
            startPos++;
        }
        addBits(startPos);
        return ((num - ((i != 0) ? decTab[i - 1] : 0)) >> (16 - startPos)) +
               posTab[startPos];
    }
}

/* */
